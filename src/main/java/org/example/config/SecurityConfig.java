package org.example.config;

import java.util.List;
import org.example.security.CsrfTokenLogger;
import org.example.security.RequestValidationFilter;
import org.example.security.UserAuthorities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private static final String WRITE_AUTHORITY = UserAuthorities.fromValue(UserAuthorities.WRITE);
    private static final String READ_AUTHORITY = UserAuthorities.fromValue(UserAuthorities.READ);

    private final RequestValidationFilter requestValidationFilter;
    private final AuthenticationProvider customAuthenticationProvider;
    private final int port;

    public SecurityConfig(RequestValidationFilter requestValidationFilter,
                          AuthenticationProvider customAuthenticationProvider, @Value("${server.port}") int port) {
        this.requestValidationFilter = requestValidationFilter;
        this.customAuthenticationProvider = customAuthenticationProvider;
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
                c -> c.disable()
        );

        http.httpBasic(Customizer.withDefaults());
        http.authenticationProvider(customAuthenticationProvider);
        http.authorizeHttpRequests(
                c -> c.requestMatchers(HttpMethod.GET, "/hello").hasAnyAuthority(WRITE_AUTHORITY, READ_AUTHORITY)
                        .requestMatchers(HttpMethod.POST, "/hello").hasAnyAuthority(WRITE_AUTHORITY)
                        .requestMatchers(HttpMethod.GET, "/hello/private").hasAnyAuthority(WRITE_AUTHORITY)
                        .anyRequest().authenticated() // All other requests require authentication
        );

        return http.build();
    }
}
