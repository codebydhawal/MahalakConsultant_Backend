package com.mahalak.media.repository;


import com.mahalak.media.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
   Optional<Role> findByRole(String role);
}
