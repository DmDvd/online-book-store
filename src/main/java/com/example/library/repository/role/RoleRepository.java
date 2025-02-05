package com.example.library.repository.role;

import com.example.library.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @EntityGraph
    Optional<Role> findByName(Role.RoleName name);
}
