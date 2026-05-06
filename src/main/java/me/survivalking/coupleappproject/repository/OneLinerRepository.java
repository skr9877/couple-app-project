package me.survivalking.coupleappproject.repository;

import me.survivalking.coupleappproject.entity.OneLiner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OneLinerRepository extends JpaRepository<OneLiner, Long> {
    List<OneLiner> findByDate(LocalDate date);
    Optional<OneLiner> findByDateAndWho(LocalDate date, String who);
}
