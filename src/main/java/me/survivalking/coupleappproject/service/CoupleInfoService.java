package me.survivalking.coupleappproject.service;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.CoupleInfo;
import me.survivalking.coupleappproject.repository.CoupleInfoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CoupleInfoService {

    private final CoupleInfoRepository repository;

    public LocalDate getStartDate() {
        return repository.findById(1L)
                .map(CoupleInfo::getStartDate)
                .orElse(null);
    }

    public LocalDate saveStartDate(LocalDate date) {
        CoupleInfo info = repository.findById(1L).orElse(new CoupleInfo());
        info.setId(1L);
        info.setStartDate(date);
        return repository.save(info).getStartDate();
    }
}
