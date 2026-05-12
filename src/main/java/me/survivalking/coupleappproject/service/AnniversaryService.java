package me.survivalking.coupleappproject.service;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.Anniversary;
import me.survivalking.coupleappproject.repository.AnniversaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnniversaryService {

    private final AnniversaryRepository anniversaryRepository;

    public Anniversary save(String title, int month, int day, String memo, String who) {
        Anniversary a = new Anniversary();
        a.setTitle(title);
        a.setMonth(month);
        a.setDay(day);
        a.setMemo(memo);
        a.setWho(who);
        return anniversaryRepository.save(a);
    }

    public List<Anniversary> findAll() {
        return anniversaryRepository.findAll();
    }

    public java.util.Optional<Anniversary> findById(Long id) {
        return anniversaryRepository.findById(id);
    }

    public Anniversary update(Long id, String title, int month, int day, String memo, String who) {
        Anniversary a = anniversaryRepository.findById(id).orElseThrow();
        a.setTitle(title);
        a.setMonth(month);
        a.setDay(day);
        a.setMemo(memo);
        a.setWho(who);
        return anniversaryRepository.save(a);
    }

    public void delete(Long id) {
        anniversaryRepository.deleteById(id);
    }
}
