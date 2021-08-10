package cloud.ptl.povserver.controler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/ping")
public class PingController {
    @GetMapping()
    public ResponseEntity<String> ping(
            @RequestParam(required = false) String message
    ) {
        return ResponseEntity.ok(Objects.requireNonNullElse(message, ""));
    }
}
