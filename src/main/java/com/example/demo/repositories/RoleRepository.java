package com.example.demo.repositories;

import com.example.demo.enums.ERole;
import com.example.demo.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
