package com.example.samuraitravel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class ReviewService {

    private final HouseRepository houseRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(HouseRepository houseRepository, ReviewRepository reviewRepository, UserRepository userRepository) {
        this.houseRepository = houseRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }
    
    public Optional<Review> getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Transactional
    public void saveReview(Integer houseId, ReviewForm reviewForm, String email) {
        // 宿泊施設の取得
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new RuntimeException("宿泊施設が見つかりません"));
        
        // ユーザーの取得
        User user = userRepository.findByEmail(email)
        	    .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + email));

        	// レビューの作成
        	Review review = new Review();
        	review.setHouse(house);
        	review.setEvaluation(reviewForm.getEvaluation());
        	review.setReview(reviewForm.getReview());
        	review.setUser(user); // ログイン中のユーザーを設定

        // レビューの保存
        reviewRepository.save(review);
    }
    
    // 最新のレビューを取得
    public List<Review> getLatestReviews(int houseId) {
        return reviewRepository.findTop4ByHouse_IdOrderByUpdatedAtDesc(houseId);
    }

    // すべてのレビューを取得
    public List<Review> getAllReviews(int houseId, String currentUserEmail) {
        List<Review> reviews = reviewRepository.findByHouse_IdOrderByUpdatedAtDesc(houseId);

        for (Review review : reviews) {
            // ログインユーザーとレビュー投稿者を比較
            boolean isEditable = currentUserEmail != null && review.getUser().getEmail().equals(currentUserEmail);
            review.setEditable(isEditable);
        }

        return reviews;
    }
    public Page<Review> getPagedReviews(Integer houseId, Pageable pageable, String currentUserEmail) {
        Page<Review> reviews = reviewRepository.findByHouse_IdOrderByUpdatedAtDesc(houseId, pageable);

        reviews.forEach(review -> {
            boolean isEditable = currentUserEmail != null 
                                 && review.getUser() != null 
                                 && currentUserEmail.equals(review.getUser().getEmail());
            review.setEditable(isEditable);
        });

        return reviews;
    }
        
    @Transactional
    public void updateReview(Integer reviewId, ReviewForm reviewForm, String email) {
        // レビューを取得し、存在しない場合は例外をスロー
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("レビューが見つかりません"));

        // ユーザーの確認（レビュー作成者とログインユーザーが一致することを確認）
        if (!review.getUser().getEmail().equals(email)) {
            throw new RuntimeException("権限がありません");
        }


        // レビューの内容を更新
        review.setEvaluation(reviewForm.getEvaluation());
        review.setReview(reviewForm.getReview());

        // 保存
        reviewRepository.save(review);
    }
    
    
    public void deleteReview(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }

}
