package me.survivalking.coupleappproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "COUPLE_INFO")
@Getter
@Setter
public class CoupleInfo {

    @Id
    private Long id;

    private LocalDate startDate;
}
