package org.ecom.user.model;

import lombok.Data;
import org.ecom.common.model.user.UserRole;

import java.util.Date;

@Data
public class UserDto
{
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNo;
    private UserRole typeOfUser;
    private Date createdOn;
    private Boolean isAccountDeleted;
    private Boolean isAccountExpired;
    private Boolean isAccountLocked;
}
