package org.example.config;

import java.util.List;
import org.example.model.UserAuthorities;
import org.example.security.CsrfTokenLogger;
import org.example.security.RequestValidationFilter;
import org.example.security.Sha512PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private static final String ADMIN_AUTHORITY = UserAuthorities.fromValue(UserAuthorities.ADMIN);
    private static final String WRITE_AUTHORITY = UserAuthorities.fromValue(UserAuthorities.WRITE);
    private static final String READ_AUTHORITY = UserAuthorities.fromValue(UserAuthorities.READ);

    private final RequestValidationFilter requestValidationFilter;
    private final int port;

    public SecurityConfig(RequestValidationFilter requestValidationFilter,
                          @Value("${server.port}") int port) {
        this.requestValidationFilter = requestValidationFilter;
        this.port = port;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(requestValidationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new CsrfTokenLogger(), CsrfFilter.class);

        http.cors(c -> {
            CorsConfigurationSource source = request -> {
                final String allowedOrigin = "http://localhost:%d".formatted(port);
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(List.of(allowedOrigin));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
                config.setAllowedHeaders(List.of("*"));
                return config;
            };
            c.configurationSource(source);
        });

        // Temporarily disabled for testing
        http.csrf(
                AbstractHttpConfigurer::disable
        );

        http.httpBasic(Customizer.withDefaults());
        http.authorizeHttpRequests(
                c -> c
                        .requestMatchers(HttpMethod.POST, "/register").hasAnyAuthority(ADMIN_AUTHORITY)
                        .requestMatchers(HttpMethod.PATCH, "/register").hasAnyAuthority(ADMIN_AUTHORITY)
                        .anyRequest().authenticated() // All other requests require authentication
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Sha512PasswordEncoder();
    }
}
