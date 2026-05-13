package me.survivalking.coupleappproject.controller;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.service.CoupleInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/couple-info")
@RequiredArgsConstructor
public class CoupleInfoController {

    private final CoupleInfoService coupleInfoService;

    @GetMapping
    public ResponseEntity<Map<String, String>> get() {
        LocalDate date = coupleInfoService.getStartDate();
        Map<String, String> result = new HashMap<>();
        result.put("startDate", date != null ? date.toString() : null);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> update(@RequestBody Map<String, String> body) {
        LocalDate date = LocalDate.parse(body.get("startDate"));
        LocalDate saved = coupleInfoService.saveStartDate(date);
        Map<String, String> result = new HashMap<>();
        result.put("startDate", saved.toString());
        return ResponseEntity.ok(result);
    }
}
