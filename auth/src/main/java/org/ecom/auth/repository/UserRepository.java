package org.ecom.auth.repository;

import org.ecom.common.model.user.*;
import org.springframework.data.mongodb.repository.*;

import java.util.*;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByusername(String username);
}
