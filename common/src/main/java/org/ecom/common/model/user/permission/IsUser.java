package org.ecom.common.model.user.permission;

import jakarta.annotation.security.*;
import org.springframework.security.access.prepost.*;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RolesAllowed({"USER"})
public @interface IsUser {
}
