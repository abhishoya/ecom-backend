package org.ecom.common.model.user;

import com.mongodb.lang.NonNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;


@Document(collection="users")
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class User implements Serializable {
    @Id
    private String id;

    @Field(name = "firstName")
    @NonNull
    private String firstName;

    @Field(name = "lastName")
    @NonNull
    private String lastName;

    @Field(name = "phoneNo")
    @NonNull
    private long phoneNo;

    @Field(name = "username")
    @NonNull
    private String username;

    @Field(name = "password")
    @BsonIgnore
    private String password;

    @Field(name = "typeOfUser")
    @NonNull
    private UserRole typeOfUser;

    @Field
    @NonNull
    private Date createdOn;

    @Field(name = "isAccountDeleted")
    private Boolean isAccountDeleted;

    @Field(name = "isAccountExpired")
    private Boolean isAccountExpired;

    @Field(name = "isAccountLocked")
    private Boolean isAccountLocked;

    @Override
    public String toString()
    {
        if(typeOfUser.equals(UserRole.ADMIN)) {
            return "User [id=" + id + ", uname=" + username + ", Admin User]";
        } else {
            return "User [id=" + id + ", uname=" + username + "]";
        }
    }

}

