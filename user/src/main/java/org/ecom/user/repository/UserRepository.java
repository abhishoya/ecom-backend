package org.ecom.user.repository;

import org.ecom.common.model.user.*;
import org.springframework.data.mongodb.repository.*;

import java.util.*;

public interface UserRepository extends MongoRepository<User, String> {
    public Optional<User> findByUsername(String username);
    public void deleteByUsername(String username);
}
