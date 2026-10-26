
package com.itassettracker.it_asset_tracker.config;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itassettracker.it_asset_tracker.dto.ErrorResponse;
import com.itassettracker.it_asset_tracker.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectMapper objectMapper) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                .requestMatchers("/api/auth/register").permitAll()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/categories",
                    "/api/assets",
                    "/api/assets/my-assets"
                ).hasAnyRole("ADMIN", "STAFF")

                .requestMatchers(
                    "/api/categories/**",
                    "/api/assets/**"
                ).hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .httpBasic(basic -> {})

            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    ErrorResponse error = new ErrorResponse(
                        LocalDateTime.now(),
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Authentication required or credentials are invalid.",
                        request.getRequestURI()
                    );

                    objectMapper.writeValue(response.getOutputStream(), error);
                })

                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    ErrorResponse error = new ErrorResponse(
                        LocalDateTime.now(),
                        HttpServletResponse.SC_FORBIDDEN,
                        "You do not have permission to perform this action.",
                        request.getRequestURI()
                    );

                    objectMapper.writeValue(response.getOutputStream(), error);
                })
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            HttpSecurity http,
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) throws Exception {

        AuthenticationManagerBuilder builder =
            http.getSharedObject(AuthenticationManagerBuilder.class);

        builder.userDetailsService(userDetailsService)
               .passwordEncoder(passwordEncoder);

        return builder.build();
    }
}
