package me.survivalking.coupleappproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "FAVORITE_PLACE")
@Getter
@Setter
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeId;
    private String placeName;
    private String address;
    private Double lat;
    private Double lng;
    private String region;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
