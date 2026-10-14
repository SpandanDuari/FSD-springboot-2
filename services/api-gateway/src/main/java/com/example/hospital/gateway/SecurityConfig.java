package com.example.hospital.gateway;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
@Configuration @EnableWebFluxSecurity public class SecurityConfig {
    @Bean SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(c -> c.disable()).authorizeExchange(a -> a.pathMatchers("/auth/**").permitAll()
                .anyExchange().authenticated()).oauth2ResourceServer(o -> o.jwt(j -> {})).build();
    }
}
