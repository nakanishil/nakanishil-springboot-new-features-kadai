package com.example.samuraitravel.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
        
    @Column(name = "furigana", length = 100, nullable = false)
    private String furigana;    
        
    @Column(name = "postal_code")
    private String postalCode;
        
    @Column(name = "address")
    private String address;
        
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "email", unique = true, nullable = false)
    private String email;
        
    @Column(name = "password")
    private String password;    
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;   
    
    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;  
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-favorites") // 修正済み
    private List<Favorite> favorites;

    
    public String getUsername() {
        return this.email; // email をユーザー名として扱う
    }
}