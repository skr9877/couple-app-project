package me.survivalking.coupleappproject.repository;

import me.survivalking.coupleappproject.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
