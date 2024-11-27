package com.example.samuraitravel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    // ユーザーIDでお気に入りを取得
    List<Favorite> findByUserId(Integer userId);

    // 宿IDでお気に入りを取得
    List<Favorite> findByHouseId(Integer houseId);

    // ユーザーIDと宿IDでお気に入りを取得
    Optional<Favorite> findByUserIdAndHouseId(Integer userId, Integer houseId);

    // ページングでお気に入りを取得
    Page<Favorite> findByUserId(Integer userId, Pageable pageable);

    // ユーザーIDと宿IDでお気に入りの存在を確認
    boolean existsByUserIdAndHouseId(Integer userId, Integer houseId);
}