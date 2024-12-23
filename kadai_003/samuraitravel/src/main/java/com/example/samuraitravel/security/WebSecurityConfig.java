package com.example.samuraitravel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests((requests) -> requests
        	    .requestMatchers("/css/**", "/images/**", "/js/**", "/storage/**", "/", "/signup/**").permitAll()
        	    .requestMatchers("/houses/**").permitAll() // 民宿詳細ページと一覧ページは認証不要
        	    .requestMatchers("/houses/{id}/reviews").permitAll() // レビュー一覧ページも認証不要
        	    .requestMatchers("/favorites/**").authenticated() // お気に入り関連全てに認証必須
        	    .requestMatchers("/admin/**").hasRole("ADMIN") // 管理者専用
        	    .anyRequest().authenticated() // その他は認証必須
        	)

            .formLogin((form) -> form
                .loginPage("/login") // ログインページ
                .loginProcessingUrl("/login") // ログイン処理
                .defaultSuccessUrl("/?loggedIn") // ログイン成功時のリダイレクト先
                .failureUrl("/login?error") // ログイン失敗時のリダイレクト先
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutSuccessUrl("/?loggedOut") // ログアウト成功時のリダイレクト先
                .permitAll()
            )
            .csrf(csrf -> csrf // 必要に応じてCSRF保護を無効化
                .ignoringRequestMatchers(
                    "/stripe/webhook", 
                    "/reviews/**", 
                    "/houses/reviews/**",
                    "/favorites/**"
                )
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
