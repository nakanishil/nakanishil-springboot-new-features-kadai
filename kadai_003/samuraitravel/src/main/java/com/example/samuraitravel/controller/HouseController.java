package com.example.samuraitravel.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.service.FavoriteService;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReviewService;
import com.example.samuraitravel.service.UserService;







@Controller
@RequestMapping("/houses")
public class HouseController {
	private final HouseRepository houseRepository;   
	private final HouseService houseService;
	private final ReviewService reviewService; // レビュー用サービスを追加
	private final FavoriteService favoriteService;
	private final UserService userService;
	

	public HouseController(HouseRepository houseRepository, HouseService houseService, ReviewService reviewService,
            FavoriteService favoriteService, UserService userService) {
		this.houseRepository = houseRepository;  
		this.houseService = houseService;
		this.reviewService = reviewService;
	    this.favoriteService = favoriteService;
	    this.userService = userService;
	}     

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,  
			@RequestParam(name = "order", required = false) String order,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) 
	{
		Page<House> housePage;

		if (keyword != null && !keyword.isEmpty()) {            
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
			} else {
				housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
			}            
		} else if (area != null && !area.isEmpty()) {            
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
			} else {
				housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
			}            
		} else if (price != null) {            
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
			} else {
				housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}            
		} else {            
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
			} else {
				housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);   
			}            
		}                

		model.addAttribute("housePage", housePage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price); 
		model.addAttribute("order", order);

		return "houses/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id,
			Model model, 
			Principal principal,
			@AuthenticationPrincipal UserDetails userDetails) {
		// 宿泊施設の情報を取得
		House house = houseService.getHouseById(id)
				.orElseThrow(() -> new RuntimeException("指定された宿泊施設が見つかりません"));

		// ログインユーザーの情報を取得
		String currentUsername = principal != null ? userDetails.getUsername() : null;

		 // ログイン状態を判定
	    boolean isLoggedIn = principal != null;

	    // お気に入り状態の判定
	    boolean isFavorited = false;
	    Integer favoriteId = null;
	    if (isLoggedIn) {
	        // ログインユーザーの情報を取得
	        String email = principal.getName();
	        User user = userService.findByEmail(email);

	        // お気に入りを取得
	        Optional<Favorite> favorite = favoriteService.getFavoriteByUserIdAndHouseId(user.getId(), house.getId());
	        if (favorite.isPresent()) {
	            isFavorited = true;
	            favoriteId = favorite.get().getFavoriteId();
	        }
	    }

		// レビューがnullの場合に空のリストを設定
		if (house.getReviews() == null) {
			house.setReviews(new ArrayList<>());
		}

		// レビューを更新日付でソートして上位4件を取得
		List<Review> latestReviews = house.getReviews().stream()
				.sorted((r1, r2) -> r2.getUpdatedAt().compareTo(r1.getUpdatedAt()))
				.limit(4)
				.toList();
		
	    // ログインユーザーが投稿したレビューには editable を設定
	    if (currentUsername != null) {
	        latestReviews.forEach(review -> {
	            review.setEditable(review.getUser().getUsername().equals(currentUsername));
	        });
	    }




		// データをモデルに追加
		model.addAttribute("house", house);  
		model.addAttribute("latestReviews", latestReviews); // 上位4件のレビュー
		model.addAttribute("reservationInputForm", new ReservationInputForm());
		model.addAttribute("isLoggedIn", isLoggedIn); // ログイン状態を追加
		model.addAttribute("isFavorited", isFavorited); // お気に入り状態を追加
		model.addAttribute("favoriteId", favoriteId); // お気に入りIDを追加

		return "houses/show"; // ビュー名
	}  

	@PostMapping("/{id}/post")
	public String submitReview(@PathVariable(name = "id") Integer houseId,
			@ModelAttribute ReviewForm reviewForm,
			Principal principal) {
		if (principal == null) {
			// 未ログイン時の処理（例: エラーメッセージを表示してリダイレクト）
			return "redirect:/login";
		}

		// レビューを保存するサービスを呼び出す
		reviewService.saveReview(houseId, reviewForm, principal.getName());
		return "redirect:/houses/" + houseId; // 宿泊施設詳細ページにリダイレクト
	}

	@GetMapping("/{id}/reviews")
	public String allReviews(@PathVariable(name = "id") Integer houseId,
			@PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Direction.DESC) Pageable pageable,
			Model model,
			Principal principal) {
		// 宿泊施設の取得
		House house = houseService.getHouseById(houseId)
				.orElseThrow(() -> new RuntimeException("指定された宿泊施設が見つかりません: " + houseId));

		// 現在のユーザー情報を取得
		String currentUserEmail = principal != null ? principal.getName() : null;

		// ページングされたレビューの取得
		Page<Review> reviewPage = reviewService.getPagedReviews(houseId, pageable, currentUserEmail);

		// データをモデルに追加
		model.addAttribute("house", house); // 宿泊施設情報
		model.addAttribute("reviewPage", reviewPage); // ページングされたレビュー

		return "houses/allReviews"; // ビュー名
	}

	@GetMapping("/{id}/reviews/post")
	public String showReviewPostPage(@PathVariable("id") Integer houseId, Model model) {
		// 指定された民宿の情報を取得
		House house = houseService.getHouseById(houseId)
				.orElseThrow(() -> new RuntimeException("宿泊施設が見つかりません: " + houseId));

		// モデルに民宿情報を追加
		model.addAttribute("house", house);

		// reviewPost.html を表示
		return "houses/reviewPost"; 
	}
	
	@GetMapping("/{houseId}/reviews/edit/{reviewId}")
	public String showEditReviewPage(@PathVariable Integer houseId,
	                                 @PathVariable Integer reviewId,
	                                 Model model,
	                                 Principal principal) {
	    // レビューを取得
	    Review review = reviewService.getReviewById(reviewId)
	            .orElseThrow(() -> new RuntimeException("レビューが見つかりません: " + reviewId));

	    // ログインユーザーとレビュー投稿者が一致するか確認
	    if (principal == null || !review.getUser().getEmail().equals(principal.getName())) {
	        throw new RuntimeException("このレビューを編集する権限がありません。");
	    }

	    // 宿泊施設を取得
	    House house = houseService.getHouseById(houseId)
	            .orElseThrow(() -> new RuntimeException("宿泊施設が見つかりません: " + houseId));

	    // モデルにデータを追加
	    model.addAttribute("house", house);
	    model.addAttribute("review", review);

	    return "houses/reviewEdit"; // 編集ページのテンプレート名
	}
	
	@PostMapping("/{id}/update") // パスを修正
	public String updateReview(
	        @PathVariable("id") Integer reviewId,
	        @Valid @ModelAttribute ReviewForm reviewForm,
	        BindingResult bindingResult,
	        Principal principal) {
		
		System.out.println("Review ID: " + reviewId);
	    
	    // デバッグ用ログ
	    System.out.println("Received POST request to update review with ID: " + reviewId);
	    System.out.println("ReviewForm content: " + reviewForm);
	    System.out.println("Principal: " + principal);

	    // ログイン状態の確認
	    if (principal == null) {
	        return "redirect:/login";
	    }

	    // バリデーションエラー時の処理
	    if (bindingResult.hasErrors()) {
	        System.out.println("Validation errors found."); // デバッグログ
	        return "houses/reviewEdit";
	    }

	    // レビューの更新処理
	    reviewService.updateReview(reviewId, reviewForm, principal.getName());

	    // レビュー取得とリダイレクト先の決定
	    Review review = reviewService.getReviewById(reviewId)
	            .orElseThrow(() -> new RuntimeException("レビューが見つかりません"));
	    
	    System.out.println("Review updated successfully."); // デバッグログ

	    return "redirect:/houses/" + review.getHouse().getId();
	}
	
	@PostMapping("/reviews/{id}/delete")
	public String deleteReview(@PathVariable("id") Integer reviewId, Principal principal) {
	    System.out.println("Delete request received for reviewId: " + reviewId);

	    // ここにデバッグログを追加
	    System.out.println("DEBUG: Entering deleteReview method");

	    if (principal == null) {
	        System.out.println("User is not logged in.");
	        return "redirect:/login";
	    }

	    System.out.println("DEBUG: Principal name: " + principal.getName());

	    Review review = reviewService.getReviewById(reviewId)
	            .orElseThrow(() -> new RuntimeException("レビューが見つかりません"));
	    System.out.println("DEBUG: Review found: " + review);

	    if (!review.getUser().getUsername().equals(principal.getName())) {
	        System.out.println("User is not authorized to delete this review.");
	        throw new RuntimeException("このレビューを削除する権限がありません。");
	    }

	    reviewService.deleteReview(reviewId);
	    System.out.println("DEBUG: Review deleted successfully.");

	    return "redirect:/houses/" + review.getHouse().getId();
	}

	
}
