package com.example.samuraitravel.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.service.FavoriteService;
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

	private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

	private FavoriteService favoriteService;
	private UserService userService;

	// コンストラクタインジェクションを使用
	public FavoriteController(FavoriteService favoriteService, UserService userService) {
		this.favoriteService = favoriteService;
		this.userService = userService;
	}

	// ユーザーIDでお気に入り取得
	@GetMapping("/user/{userId}")
	public ResponseEntity<Page<Favorite>> getFavoritesByUserId(
	        @PathVariable Integer userId,
	        @PageableDefault(page = 0, size = 10, sort = "favoriteDay", direction = Direction.DESC) Pageable pageable) {
	    logger.info("Fetching paginated favorites for userId: {}", userId);
	    Page<Favorite> favoritePage = favoriteService.getFavoritesByUserId(userId, pageable);
	    if (favoritePage.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
	    return ResponseEntity.ok(favoritePage);
	}


	// 宿IDでお気に入り取得
	@GetMapping("/house/{houseId}")
	public List<Favorite> getFavoritesByHouseId(@PathVariable Integer houseId) {
		logger.info("Fetching favorites for houseId: {}", houseId);
		return favoriteService.getFavoritesByHouseId(houseId);
	}

	// お気に入り登録
	
	@PostMapping
	public String saveFavorite(@ModelAttribute Favorite favorite, Principal principal) {
	    // ログインユーザーを取得
	    String email = principal.getName();
	    User user = userService.findByEmail(email);
	    if (user == null) {
	        return "redirect:/login";
	    }

	    // ユーザーをお気に入りにセット
	    favorite.setUser(user);

	    // 必要なチェック（例: House が null ではないか確認）
	    if (favorite.getHouse() == null || favorite.getHouse().getId() == null) {
	        return "redirect:/houses"; // エラーメッセージを追加してリダイレクトするのが理想
	    }

	    // お気に入りを保存
	    favoriteService.saveFavorite(favorite);

	    // 宿詳細ページにリダイレクト
	    return "redirect:/houses/" + favorite.getHouse().getId();
	}

//	@PostMapping
//	public String saveFavorite(Favorite favorite, Principal principal) {
//		// ログインユーザーを取得
//		String email = principal.getName();
//		User user = userService.findByEmail(email);
//		if (user == null) {
//			return "redirect:/login";
//		}
//
//		// ユーザーをお気に入りにセット
//		favorite.setUser(user);
//
//		// お気に入りを保存
//		favoriteService.saveFavorite(favorite);
//
//		// 宿詳細ページにリダイレクト
//		return "redirect:/houses/" + favorite.getHouse().getId();
//	}

	@DeleteMapping("/{favoriteId}")
	public String deleteFavorite(@PathVariable Integer favoriteId, Principal principal) {
	    Favorite favorite = favoriteService.getFavoriteById(favoriteId);
	    if (favorite == null) {
	        // エラーメッセージを表示するなどの処理（例えばリダイレクト）
	        return "redirect:/favorites/list";
	    }

	    // ログインユーザーが所有するお気に入りか確認
	    String email = principal.getName();
	    if (!favorite.getUser().getEmail().equals(email)) {
	        return "redirect:/favorites/list"; // ユーザー権限がない場合
	    }

	    favoriteService.deleteFavorite(favoriteId);
	    return "redirect:/houses/" + favorite.getHouse().getId(); // 宿詳細ページにリダイレクト
	}

	
//	@DeleteMapping("/{favoriteId}")
//	public String deleteFavorite(@PathVariable Integer favoriteId) {
//		Favorite favorite = favoriteService.getFavoriteById(favoriteId);
//		if (favorite == null) {
//			// エラーメッセージを表示するなどの処理
//			return "redirect:/houses";
//		}
//		favoriteService.deleteFavorite(favoriteId);
//		return "redirect:/houses/" + favorite.getHouse().getId(); // 宿詳細ページにリダイレクト
//	}

	// ビューを返す
	
	@GetMapping("/list")
	public String viewFavorites(
	    @PageableDefault(page = 0, size = 10, sort = "favoriteDay", direction = Direction.DESC) Pageable pageable,
	    Model model,
	    Principal principal
	) {
	    // ログイン済みのユーザーを取得
	    String email = principal.getName();
	    User user = userService.findByEmail(email);
	    if (user == null) {
	        return "redirect:/login";
	    }

	    // ページングされたお気に入りを取得
	    Page<Favorite> favoritePage = favoriteService.getFavoritesByUserId(user.getId(), pageable);
	    // モデルにデータを追加
	    model.addAttribute("favoritePage", favoritePage);
	    model.addAttribute("currentPage", pageable.getPageNumber());
	    model.addAttribute("totalPages", favoritePage.getTotalPages());
	    // favorites.htmlで使うリストをセット
	    model.addAttribute("favorites", favoritePage.getContent());

	    return "user/favorites";
	}

//	@GetMapping("/list")
//	public String viewFavorites(
//	    @PageableDefault(page = 0, size = 10, sort = "favoriteDay", direction = Direction.DESC) Pageable pageable,
//	    Model model,
//	    Principal principal
//	) {
//	    // ログイン済みのユーザーを取得
//	    String email = principal.getName();
//	    User user = userService.findByEmail(email);
//	    if (user == null) {
//	        return "redirect:/login";
//	    }
//
//	    // ページングされたお気に入りを取得
//	    Page<Favorite> favoritePage = favoriteService.getFavoritesByUserId(user.getId(), pageable);
//
//	    // モデルにデータを追加
//	    model.addAttribute("favoritePage", favoritePage);
//	    model.addAttribute("currentPage", pageable.getPageNumber());
//	    model.addAttribute("totalPages", favoritePage.getTotalPages());
//
//	    return "user/favorites";
//	}
//	@GetMapping("/list")
//	public String viewFavorites(Model model) {
//		// 認証済みのユーザーを取得
//		String email = SecurityContextHolder.getContext().getAuthentication().getName();
//		User user = userService.findByEmail(email); // emailからユーザーを取得
//		if (user == null) {
//			logger.error("認証済みのユーザーが見つかりません");
//			return "redirect:/login"; // ログインページにリダイレクト
//		}
//
//		Integer userId = user.getId();
//		List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
//		model.addAttribute("favorites", favorites);
//		return "user/favorites";
//	}






}
