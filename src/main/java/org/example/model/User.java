package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Document(collection = "account")
public class User implements UserDetails {

    @Id
    @JsonProperty("_id")
    private String id;
    private String username;
    private String password;
    private String authority;
    private boolean isEnabled;
    private String salt;

    public String getId() {
        return id;
    }

    public User id(String id) {
        this.id = id;
        return this;
    }

    public User username(String username) {
        this.username = username;
        return this;
    }

    public User password(String password) {
        this.password = password;
        return this;
    }

    public String getAuthority() {
        return authority;
    }

    public User authority(String authority) {
        this.authority = authority;
        return this;
    }

    public User enabled(boolean enabled) {
        isEnabled = enabled;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public User salt(String salt) {
        this.salt = salt;
        return this;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
