package me.survivalking.coupleappproject.controller;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.OneLiner;
import me.survivalking.coupleappproject.service.OneLinerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/oneliners")
@RequiredArgsConstructor
public class OneLinerController {

    private final OneLinerService oneLinerService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> findByDate(@RequestParam(defaultValue = "") String date) {
        LocalDate target = date.isBlank() ? LocalDate.now() : LocalDate.parse(date);
        List<Map<String, Object>> result = oneLinerService.findByDate(target).stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody Map<String, String> body) {
        LocalDate date = body.get("date") != null ? LocalDate.parse(body.get("date")) : LocalDate.now();
        OneLiner saved = oneLinerService.save(date, body.get("who"), body.get("content"));
        return ResponseEntity.ok(toMap(saved));
    }

    private Map<String, Object> toMap(OneLiner o) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", o.getId());
        map.put("date", o.getDate().toString());
        map.put("who", o.getWho());
        map.put("content", o.getContent());
        map.put("createdAt", o.getCreatedAt().toString());
        return map;
    }
}
