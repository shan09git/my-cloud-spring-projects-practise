package app.myappusers;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class AuthUser extends User {

    private final UserEntity user;

    public AuthUser(UserEntity userEntity) {
        super(userEntity.getEmail(), userEntity.getPassword(), new ArrayList<>());
        this.user = userEntity;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Set.of(() -> "read");
    }

    @Override
    public String getPassword() {
        return user.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return true;
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
}
