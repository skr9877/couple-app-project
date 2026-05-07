package me.survivalking.coupleappproject.repository;

import me.survivalking.coupleappproject.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByPlaceId(String placeId);
}
