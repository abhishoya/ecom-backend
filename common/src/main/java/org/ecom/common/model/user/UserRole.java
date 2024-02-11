package org.ecom.common.model.user;

import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;

import java.util.*;
import java.util.stream.*;

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
