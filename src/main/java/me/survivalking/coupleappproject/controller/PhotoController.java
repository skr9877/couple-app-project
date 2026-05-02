package me.survivalking.coupleappproject.controller;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.Photo;
import me.survivalking.coupleappproject.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping
    public ResponseEntity<Photo> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(photoService.upload(file));
    }

    @GetMapping
    public List<Photo> list(@RequestParam(required = false) String month) {
        return photoService.findAll(month);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IOException {
        photoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
