package me.survivalking.coupleappproject.controller;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.Anniversary;
import me.survivalking.coupleappproject.service.AnniversaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/anniversaries")
@RequiredArgsConstructor
public class AnniversaryController {

    private final AnniversaryService anniversaryService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Anniversary saved = anniversaryService.save(
            (String) body.get("title"),
            ((Number) body.get("month")).intValue(),
            ((Number) body.get("day")).intValue(),
            (String) body.get("memo"),
            (String) body.get("who")
        );
        return ResponseEntity.ok(toMap(saved));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> findAll() {
        List<Map<String, Object>> result = anniversaryService.findAll().stream()
            .map(this::toMap)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id) {
        return anniversaryService.findById(id)
            .map(a -> ResponseEntity.ok(toMap(a)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Anniversary updated = anniversaryService.update(
            id,
            (String) body.get("title"),
            ((Number) body.get("month")).intValue(),
            ((Number) body.get("day")).intValue(),
            (String) body.get("memo"),
            (String) body.get("who")
        );
        return ResponseEntity.ok(toMap(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        anniversaryService.delete(id);
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> toMap(Anniversary a) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", a.getId());
        map.put("title", a.getTitle());
        map.put("month", a.getMonth());
        map.put("day", a.getDay());
        map.put("memo", a.getMemo());
        map.put("who", a.getWho());
        return map;
    }
}
