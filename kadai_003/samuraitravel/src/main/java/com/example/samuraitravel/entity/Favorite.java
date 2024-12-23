package com.example.samuraitravel.entity;



import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favorites") // この行を追加
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "favoriteId")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favoriteId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-favorites") // 対応するバックリファレンス
    private User user;


    @ManyToOne
    @JoinColumn(name = "house_id", nullable = false)
    @JsonBackReference("house-favorites") // 民宿のリレーションを逆参照
    private House house;

    @Column(name = "comment", nullable = true, length = 200) // コメントはNULL許容
    private String comment;

    @Column(name = "favorite_day", nullable = false, updatable = false)
    private LocalDateTime favoriteDay = LocalDateTime.now();

}
