package com.ssginc.showping.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    ////////////////////////////////////////////////////////////////////////////////////////////
    ///  3. custome한 login form있는 html파일 지정 ===> jwt적용
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .cors(cors -> cors.configurationSource(request -> {
//                    CorsConfiguration config = new CorsConfiguration();
//                    config.setAllowedOrigins(List.of("http://localhost:8888")); // 프론트엔드 주소
//                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//                    config.setAllowedHeaders(List.of("*"));
//                    config.setAllowCredentials(true);
//                    return config;
//                }))
                .csrf(csrf -> csrf.disable())
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // CSRF 토큰을 쿠키에 저장
//                )
                .authorizeHttpRequests(auth -> auth
                        //루트는 모두 허용
                        //.requestMatchers("/", "/login", "/logout", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                        .requestMatchers("/", "/live/live", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                        // 정적 리소스 접근 허용, 싱글톤빈으로 static주소(resources아래 폴더) 접근 제어 가능
                        .requestMatchers("/admin/**", "/chat/chat").hasRole("ADMIN") //admin, chat/chat 주소 접근은 ADMIN role만 접근 가능
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") //user 주소 접근은 ADMIN/USER role중 어떤 것이든 접근 가능
                        //.anyRequest().authenticated() //모두 인증필요
                        .anyRequest().permitAll() //모두 허용하고, admin과 user로 시작하는 사이트와 /chat/chat만 로그인 필요
                )
                .formLogin(login -> login
                        .loginPage("/login2")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout3")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "accessToken")
                        .permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}