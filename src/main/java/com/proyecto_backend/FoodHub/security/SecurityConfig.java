package com.proyecto_backend.FoodHub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// --- IMPORTS DE CORS (NUEVOS) ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoUserDetailsService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // --- RUTAS PUBLICAS (AGREGADO /v1) ---
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/menus/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/kioskos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll() // <--- ¡ESTE ERA EL CULPABLE!
                        .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/resenas/**").permitAll()

                        // --- RUTAS CLIENTE ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/resenas").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/resenas/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/resenas/**").hasRole("CLIENTE")

                        // --- RUTAS KIOSKERO/ADMIN ---
                        .requestMatchers(HttpMethod.PUT, "/api/v1/kioskos/**").hasAnyRole("KIOSKERO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasAnyRole("KIOSKERO", "ADMIN")

                        // --- RUTAS ADMIN ---
                        .requestMatchers("/api/v1/menus/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/categorias/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/kioskos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/kioskos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // 2. DEFINIR LA CONFIGURACIÓN DE CORS (NUEVO MÉTODO)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitir el origen de tu Frontend (Vite usa el puerto 5173 por defecto)
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}