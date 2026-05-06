package me.survivalking.coupleappproject.service;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.Schedule;
import me.survivalking.coupleappproject.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public Schedule save(String date, String title, String time, String who, String memo) {
        Schedule schedule = new Schedule();
        schedule.setDate(LocalDate.parse(date));
        schedule.setTitle(title);
        schedule.setTime(time);
        schedule.setWho(who);
        schedule.setMemo(memo);
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    public Schedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    public Schedule update(Long id, String title, String time, String who, String memo) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow();
        schedule.setTitle(title);
        schedule.setTime(time);
        schedule.setWho(who);
        schedule.setMemo(memo);
        return scheduleRepository.save(schedule);
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }
}
