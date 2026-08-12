package com.mahalak.media.auth;

import com.mahalak.media.servicesImpl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthorizationFilter jwtAuthorizationFilter) {

        this.userDetailsService = userDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            BCryptPasswordEncoder passwordEncoder) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Swagger URL's
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/rest/product/**",
                                "/rest/project/**", //search,random,getall
                                "/rest/blog/**",
                                "/rest/media/**",   //search,getMediaById,getAllMedia
                                "/rest/team/**"
                        ).permitAll()
                        .requestMatchers("/api/drive/**").permitAll()
                        .requestMatchers("/google/login", "/oauth2/callback").permitAll()
                        // authenticated URL's
                        .requestMatchers("/rest/auth/register", "/rest/auth/login").permitAll()
                        .requestMatchers("/rest/auth/**").authenticated()
                        .requestMatchers("/rest/product/delete").hasRole("ADMIN")
                        .requestMatchers("/rest/product/**").authenticated()
                        .requestMatchers("/rest/blog/pdf/**").permitAll()

                        .requestMatchers("/rest/auth/**").authenticated()
                        .requestMatchers("/rest/product/delete").hasRole("ADMIN")
                        .requestMatchers("/rest/product/**").authenticated()
                        .requestMatchers("/rest/project/delete").hasRole("ADMIN")
                        .requestMatchers("/rest/project/**").authenticated()
                        .requestMatchers("/rest/cart/**").authenticated()
                        .requestMatchers("/rest/media/**").authenticated()
                        .requestMatchers("/rest/address/**").authenticated()
                        // Pricing rules directly change customer checkout totals.
                        // Only administrators may view or modify them.
                        .requestMatchers("/rest/tax-rule/**").hasRole("ADMIN")
                        .requestMatchers("/rest/discount-rule/**").hasRole("ADMIN")
                        .requestMatchers("/rest/shipping-rule/**").hasRole("ADMIN")
                        .requestMatchers("/rest/payment/admin/**").hasRole("ADMIN")
                        .requestMatchers("/rest/payment/**").authenticated()
                        .requestMatchers("/rest/order/admin/**").hasRole("ADMIN")
                        .requestMatchers("/rest/order/**").authenticated()
                        .requestMatchers("/rest/commerce/admin/**").hasRole("ADMIN")
                        .requestMatchers("/rest/commerce/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return httpSecurity.build();
    }

}
