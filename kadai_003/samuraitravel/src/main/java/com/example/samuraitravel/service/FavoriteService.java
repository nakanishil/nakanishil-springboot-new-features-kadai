package com.example.samuraitravel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.repository.FavoriteRepository;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public Page<Favorite> getFavoritesByUserId(Integer userId, Pageable pageable) {
        return favoriteRepository.findByUserId(userId, pageable);
    }



    public List<Favorite> getFavoritesByHouseId(Integer houseId) {
        return favoriteRepository.findByHouseId(houseId);
    }

    public Favorite saveFavorite(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }

    public void deleteFavorite(Integer favoriteId) {
        favoriteRepository.deleteById(favoriteId);
    }
    public boolean existsByUserIdAndHouseId(Integer userId, Integer houseId) {
        return favoriteRepository.existsByUserIdAndHouseId(userId, houseId);
    }
    
    public Optional<Favorite> getFavoriteByUserIdAndHouseId(Integer userId, Integer houseId) {
        return favoriteRepository.findByUserIdAndHouseId(userId, houseId);
    }
    
    public Favorite getFavoriteById(Integer favoriteId) {
        return favoriteRepository.findById(favoriteId).orElse(null);
    }
    
    



}
