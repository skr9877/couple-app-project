package me.survivalking.coupleappproject.service;

import lombok.RequiredArgsConstructor;
import me.survivalking.coupleappproject.entity.Favorite;
import me.survivalking.coupleappproject.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public Favorite save(String placeId, String placeName, String address, Double lat, Double lng, String region) {
        Favorite favorite = new Favorite();
        favorite.setPlaceId(placeId);
        favorite.setPlaceName(placeName);
        favorite.setAddress(address);
        favorite.setLat(lat);
        favorite.setLng(lng);
        favorite.setRegion(region);
        return favoriteRepository.save(favorite);
    }

    public List<Favorite> findAll() {
        return favoriteRepository.findAll();
    }

    public boolean existsByPlaceId(String placeId) {
        return favoriteRepository.existsByPlaceId(placeId);
    }

    public void delete(Long id) {
        favoriteRepository.deleteById(id);
    }
}
