//package com.example.housetalk_be.common;
//
//import com.example.housetalk_be.auth.jwt.JwtAuthenticationFilter;
//import com.example.housetalk_be.user.service.CustomUserDetailsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomUserDetailsService userDetailsService;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    // 🔐 비밀번호 암호화
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // 🌐 CORS + 보안 필터 설정 + JWT 적용
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> {}) // 필요 시 CorsConfigurationSource 사용
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT는 세션 사용 X
//                .authorizeHttpRequests(auth -> auth
//                        // 회원가입, 로그인, 비밀번호 찾기 등 공개
//                        .requestMatchers("/api/users/signup/**",
//                                "/api/users/login",
//                                "/api/users/find-password",
//                                "/api/auth/**",
//                                "/api/news",
//                                "/images/**").permitAll()
//
//                        // 매물 조회(GET) 공개
//                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/houses/**").permitAll()
//
//                        // 매물 등록/수정/삭제는 로그인 필요
//                        .requestMatchers("/api/houses/**").authenticated()
//
//                        // 그 외 요청 인증 필요
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    // AuthenticationManager 정의 (로그인 시 사용)
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//
//        authBuilder.userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder());
//
//        return authBuilder.build(); // 🔑 여기서 바로 build()
//    }
//}
package com.example.housetalk_be.common;

import com.example.housetalk_be.auth.jwt.JwtAuthenticationFilter;
import com.example.housetalk_be.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 공개 API
                        .requestMatchers(
                                "/api/users/signup/**",
                                "/api/users/login",
                                "/api/users/find-password",
                                "/api/auth/**",
                                "/api/news",
                                "/images/**"
                        ).permitAll()
                        // GET 매물은 누구나 조회 가능
                        .requestMatchers(HttpMethod.GET, "/api/houses/**","/api/boards/**").permitAll()
                        // POST/PUT/DELETE는 로그인 필요
                        .requestMatchers("/api/houses/**","/api/boards/**", "/api/boards/my").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }
}