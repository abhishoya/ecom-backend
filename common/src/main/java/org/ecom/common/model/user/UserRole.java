package org.ecom.common.model.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {
    USER(new HashSet<>(Arrays.asList(UserPermission.READ_WRITE_USER))),
    ADMIN(new HashSet<>(Arrays.asList(UserPermission.READ_WRITE_ADMIN)));

    Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions=permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> s = this.permissions.stream().map(authority -> new SimpleGrantedAuthority(authority.name())).collect(Collectors.toSet());
        s.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return s;
    }
}
