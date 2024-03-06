package org.ecom.common.model.user.permission;

import jakarta.annotation.security.RolesAllowed;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RolesAllowed({"ADMIN"})
public @interface IsAdmin {
}
