package me.survivalking.coupleappproject.repository;

import me.survivalking.coupleappproject.entity.Anniversary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnniversaryRepository extends JpaRepository<Anniversary, Long> {
}
