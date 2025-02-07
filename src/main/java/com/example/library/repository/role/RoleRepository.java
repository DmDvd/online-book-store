package com.example.library.repository.role;

import com.example.library.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(Role.RoleName name);
}
