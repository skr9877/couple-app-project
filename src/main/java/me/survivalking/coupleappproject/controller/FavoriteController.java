package me.survivalking.coupleappproject.controller;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.Favorite;
import me.survivalking.coupleappproject.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> findAll() {
        List<Map<String, Object>> result = favoriteService.findAll().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Favorite saved = favoriteService.save(
                (String) body.get("placeId"),
                (String) body.get("placeName"),
                (String) body.get("address"),
                Double.parseDouble(body.get("lat").toString()),
                Double.parseDouble(body.get("lng").toString()),
                (String) body.get("region")
        );
        return ResponseEntity.ok(toMap(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        favoriteService.delete(id);
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> toMap(Favorite f) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", f.getId());
        map.put("placeId", f.getPlaceId());
        map.put("placeName", f.getPlaceName());
        map.put("address", f.getAddress());
        map.put("lat", f.getLat());
        map.put("lng", f.getLng());
        map.put("region", f.getRegion());
        return map;
    }
}
