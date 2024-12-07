package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Document(collection = "account")
public class User implements UserDetails {

    @Id
    @JsonProperty("_id")
    private String id;
    @Version
    private long version;
    private String username;
    private String password;
    private String authority;
    private boolean isEnabled;
    private String salt;
    private Updated updated;
    private Created created;

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

    public Updated getUpdated() {
        return updated;
    }

    public User updated(Updated updated) {
        this.updated = updated;
        return this;
    }

    public Created getCreated() {
        return created;
    }

    public User created(Created created) {
        this.created = created;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return isEnabled == user.isEnabled && Objects.equals(id, user.id) && Objects.equals(username,
                user.username) && Objects.equals(password, user.password) && Objects.equals(authority,
                user.authority) && Objects.equals(salt, user.salt) && Objects.equals(updated,
                user.updated) && Objects.equals(created, user.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, authority, isEnabled, salt, updated, created);
    }
}
