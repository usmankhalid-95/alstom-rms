package alstom.rms.springboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public ResponseEntity<Map<String, String>> index() {
        Map<String, String> links = new HashMap<>();
        links.put("trains", "/api/trains");
        return ResponseEntity.ok(links);
    }
}
