package org.ecom.auth.dto;

import lombok.*;
import lombok.extern.slf4j.*;
import org.ecom.common.model.user.User;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;

import java.util.*;

@Slf4j
@ToString
@RequiredArgsConstructor
public class ApplicationUsers implements UserDetails
{
    private final User userObject;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userObject.getTypeOfUser().getAuthorities();
    }

    @Override
    public String getPassword() {
        return userObject.getPassword();
    }

    @Override
    public String getUsername() {
        return userObject.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userObject.getIsAccountExpired() == null || !userObject.getIsAccountExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userObject.getIsAccountLocked() == null || !userObject.getIsAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // always non-expired credentials
    }

    @Override
    public boolean isEnabled() {
        return userObject.getIsAccountDeleted() == null || !userObject.getIsAccountDeleted();
    }
}
