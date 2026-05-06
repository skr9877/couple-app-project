package me.survivalking.coupleappproject.repository;

import me.survivalking.coupleappproject.entity.OneLiner;
import me.survivalking.coupleappproject.entity.OneLinerKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OneLinerRepository extends JpaRepository<OneLiner, OneLinerKey> {
    List<OneLiner> findByDate(LocalDate date);
}
