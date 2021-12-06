package cloud.ptl.povserver.configuration;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${ptl.api.username}")
    private String apiUsername;

    @Value("${ptl.api.password}")
    private String apiPassword;

    @Value("${ptl.console.username}")
    private String consoleUsername;

    @Value("${ptl.console.password}")
    private String consolePassword;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(this.apiUsername).password(this.passwordEncoder().encode(this.apiPassword)).roles("API")
                .and()
                .withUser(this.consoleUsername).password(this.passwordEncoder().encode(this.consolePassword)).roles("USER");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(ServletHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/portal/**").hasRole("USER")
                .regexMatchers("/frontend/.*", "/VAADIN/.*").permitAll()
                .requestMatchers(SecurityConfig::isFrameworkInternalRequest).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic()
                .and()
                // vaadin already has csrf
                .csrf().disable()
                .logout().permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                // Vaadin Flow static resources //
                "/VAADIN/**",
                // the standard favicon URI
                "/favicon.ico",
                // the robots exclusion standard
                "/robots.txt",
                // web application manifest //
                "/manifest.webmanifest",
                "/sw.js",
                "/offline-page.html",
                // (development mode) static resources //
                "/frontend/**",
                // (development mode) webjars //
                "/webjars/**",
                // (production mode) static resources //
                "/frontend-es5/**", "/frontend-es6/**",
                "/resources/**", "/error");
    }
}
