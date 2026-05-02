package me.survivalking.coupleappproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "OUR_PHOTO")
@Getter
@Setter
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFilename;
    private String storedFilename;
    private String yearMonth;    // "202605"
    private String monthKey;     // "2026-05"
    private String monthLabel;   // "2026년 5월"
    private LocalDateTime uploadDate;
}
