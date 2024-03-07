package org.ecom.user.repository;

import org.ecom.common.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    void deleteByUsername(String username);
}
