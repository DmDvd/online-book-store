package com.example.library.service;

import com.example.library.model.Role;

public interface RoleService {
    Role getRoleByName(Role.RoleName roleName);
}
