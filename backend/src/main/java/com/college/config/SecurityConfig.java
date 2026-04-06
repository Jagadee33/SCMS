package com.college.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/courses/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/performance-prediction/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/smart-attendance/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/study-optimization/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/analytics/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/notifications/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/admin/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/users/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/api/students/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/api/v1/students/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/api/fees/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/api/payments/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/api/payment-transactions/**")).authenticated()
                .requestMatchers(new AntPathRequestMatcher("/api/enrollments/**")).authenticated()
                .anyRequest().permitAll()
            );
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
