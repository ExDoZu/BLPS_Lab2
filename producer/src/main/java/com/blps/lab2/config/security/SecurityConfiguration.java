package com.blps.lab2.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz

                        .requestMatchers("/api/moderation").hasRole("MODERATOR")
                        .requestMatchers(HttpMethod.POST).hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE).hasRole("USER")
                        .requestMatchers(HttpMethod.PUT).hasRole("USER")
                        .requestMatchers("/api/posts/mine").hasRole("USER")
                        .anyRequest().permitAll())

                .httpBasic(withDefaults())

        ;
        return http.build();
    }

}