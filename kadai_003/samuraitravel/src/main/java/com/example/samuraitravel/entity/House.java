package com.example.samuraitravel.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "houses")
@Data
public class House {
	
	
	@OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("house-favorites") // 管理側の設定
	private List<Favorite> favorites;

	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "image_name")
    private String imageName;    
        
    @Column(name = "description")
    private String description;    
    
    @Column(name = "price")
    private Integer price;    
    
    @Column(name = "capacity")
    private Integer capacity;     
        
    @Column(name = "postal_code")
    private String postalCode;
        
    @Column(name = "address")
    private String address;
        
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;   
    
    
    
    // レビューとのリレーションを追加
    @ToString.Exclude
    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews;
    
    public Double getAverageRating() {
        if (this.reviews == null || this.reviews.isEmpty()) {
            return null; // レビューがない場合はnullを返す
        }
        return this.reviews.stream()
                           .mapToDouble(Review::getEvaluation) // 各評価を取得
                           .average() // 平均を計算
                           .orElse(0.0); // 平均が計算できない場合は0.0を返す
    }

}