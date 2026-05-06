package me.survivalking.coupleappproject.service;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.OneLiner;
import me.survivalking.coupleappproject.repository.OneLinerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OneLinerService {

    private final OneLinerRepository oneLinerRepository;

    public List<OneLiner> findByDate(LocalDate date) {
        return oneLinerRepository.findByDate(date);
    }

    // 같은 날 같은 who가 다시 저장하면 덮어씀
    public OneLiner save(LocalDate date, String who, String content) {
        OneLiner oneLiner = oneLinerRepository.findByDateAndWho(date, who)
                .orElse(new OneLiner());
        oneLiner.setDate(date);
        oneLiner.setWho(who);
        oneLiner.setContent(content);
        return oneLinerRepository.save(oneLiner);
    }
}
