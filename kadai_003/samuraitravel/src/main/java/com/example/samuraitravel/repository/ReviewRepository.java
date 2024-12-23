package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.samuraitravel.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 特定の宿（House）のレビューを取得
    List<Review> findByHouse_Id(int houseId);

    // 特定のユーザー（User）のレビューを取得
    List<Review> findByUser_Id(int userId);

    // 宿IDとユーザーIDに基づいてレビューを取得（オプション）
    List<Review> findByHouse_IdAndUser_Id(int houseId, int userId);
    
    // 宿IDに基づき最新のレビューを最大4件取得
    List<Review> findTop4ByHouse_IdOrderByUpdatedAtDesc(int houseId);
    
    List<Review> findByHouse_IdOrderByUpdatedAtDesc(int houseId);
    
    Page<Review> findByHouse_IdOrderByUpdatedAtDesc(int houseId, Pageable pageable);
}
