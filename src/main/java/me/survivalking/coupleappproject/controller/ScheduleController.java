package me.survivalking.coupleappproject.controller;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.Schedule;
import me.survivalking.coupleappproject.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> body) {
        Schedule saved = scheduleService.save(
                body.get("date"), body.get("title"), body.get("time"), body.get("who"), body.get("memo"));
        return ResponseEntity.ok(toMap(saved));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> findAll() {
        List<Map<String, Object>> result = scheduleService.findAll().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id) {
        Schedule s = scheduleService.findById(id);
        return s != null ? ResponseEntity.ok(toMap(s)) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Schedule updated = scheduleService.update(id, body.get("title"), body.get("time"), body.get("who"), body.get("memo"));
        return ResponseEntity.ok(toMap(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> toMap(Schedule s) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("date", s.getDate().toString());
        map.put("title", s.getTitle());
        map.put("time", s.getTime());
        map.put("who", s.getWho());
        map.put("memo", s.getMemo());
        return map;
    }
}
