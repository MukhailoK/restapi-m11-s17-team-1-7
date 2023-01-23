package com.softserve.itacademy.todolist.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .antMatchers( "/api/auth/**", "/h2/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .exceptionHandling(eh -> eh
//                        .authenticationEntryPoint(restAuthenticationEntryPoint())
//                )
//                .httpBasic(hb -> hb
//                        .authenticationEntryPoint(restAuthenticationEntryPoint()) // Handles auth error
//                )
//                .csrf().disable()
//                .headers(h -> h
//                        .frameOptions() // for Postman, the H2 console
//                        .disable()
//                )
//                .sessionManagement(sm -> sm
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
//                )
//                .authorizeHttpRequests(a -> a
//                        .antMatchers("/h2/**", "/api/users/user/create/")
//                        .permitAll()
//                        // todo
//                        .anyRequest().authenticated()
//                );
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            log.warn("Authentication for '{} {}' failed with error: {}",
                    request.getMethod(), request.getRequestURL(), authException.getMessage());
            response.sendError(
                    UNAUTHORIZED.value(), authException.getMessage());
        };
    }
}
