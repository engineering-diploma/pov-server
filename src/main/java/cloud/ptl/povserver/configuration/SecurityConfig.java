package cloud.ptl.povserver.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Configuration
    @Order(1)
    public static class ApiSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**").authorizeRequests()
                    .antMatchers("/api/**").authenticated()
                    .and()
                    .antMatcher("/api/**").httpBasic()
                    .and()
                    .csrf().disable();
        }
    }

    @Configuration
    @Order(2)
    public static class PortalSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/portal/**").authorizeRequests()
                    .antMatchers("/portal/**").authenticated()
                    .antMatchers("/portal/**").hasRole("USER")
                    .and()
                    .formLogin()
                    .and()
                    .logout().permitAll();
        }
    }
}
