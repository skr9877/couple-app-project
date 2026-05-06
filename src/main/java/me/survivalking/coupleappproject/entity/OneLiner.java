package me.survivalking.coupleappproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ONE_LINER")
@IdClass(OneLinerKey.class)
@Getter
@Setter
public class OneLiner {

    @Id
    private LocalDate date;

    @Id
    private String who;

    @Column(length = 500)
    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
