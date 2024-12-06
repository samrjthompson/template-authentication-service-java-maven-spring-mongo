package org.example.security;

import java.util.List;
import org.example.model.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final List<Class<?>> LIST_OF_SUPPORTED_AUTH_TYPES =
            List.of(UsernamePasswordAuthenticationToken.class);

    private final InMongoUserDetailsService inMongoUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(InMongoUserDetailsService inMongoUserDetailsService,
                                        PasswordEncoder passwordEncoder) {
        this.inMongoUserDetailsService = inMongoUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        User user = inMongoUserDetailsService.loadUserByUsername(username);
        final String saltedPassword = "%s%s".formatted(password, user.getSalt());

        if (passwordEncoder.matches(saltedPassword, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return LIST_OF_SUPPORTED_AUTH_TYPES.contains(authenticationType);
    }
}
