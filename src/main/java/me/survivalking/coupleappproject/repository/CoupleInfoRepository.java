package me.survivalking.coupleappproject.repository;

import me.survivalking.coupleappproject.entity.CoupleInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleInfoRepository extends JpaRepository<CoupleInfo, Long> {
}
