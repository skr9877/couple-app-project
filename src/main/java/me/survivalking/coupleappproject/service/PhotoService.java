package me.survivalking.coupleappproject.service;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.Photo;
import me.survivalking.coupleappproject.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoService {

    @Value("${app.upload.path}")
    private String uploadPath;

    private final PhotoRepository photoRepository;

    public Photo upload(MultipartFile file) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyyMM"));
        String monthKey = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String monthLabel = now.getYear() + "년 " + now.getMonthValue() + "월";

        Path dir = Paths.get(uploadPath, "image", yearMonth);
        Files.createDirectories(dir);

        String ext = getExtension(file.getOriginalFilename());
        String storedFilename = UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), dir.resolve(storedFilename));

        Photo photo = new Photo();
        photo.setOriginalFilename(file.getOriginalFilename());
        photo.setStoredFilename(storedFilename);
        photo.setYearMonth(yearMonth);
        photo.setMonthKey(monthKey);
        photo.setMonthLabel(monthLabel);
        photo.setUploadDate(now);

        return photoRepository.save(photo);
    }

    public List<Photo> findAll(String monthKey) {
        if (monthKey != null) return photoRepository.findByMonthKeyOrderByUploadDateDesc(monthKey);
        return photoRepository.findAllByOrderByUploadDateDesc();
    }

    public void delete(Long id) throws IOException {
        Photo photo = photoRepository.findById(id).orElseThrow();
        Path file = Paths.get(uploadPath, "image", photo.getYearMonth(), photo.getStoredFilename());
        Files.deleteIfExists(file);
        photoRepository.deleteById(id);
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }
}
