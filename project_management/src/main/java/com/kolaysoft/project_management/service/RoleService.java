//Rollerle ilgili basit iş mantığını içerir.

package com.kolaysoft.project_management.service;

import com.kolaysoft.project_management.entity.Role;
import com.kolaysoft.project_management.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public Optional<Role> findByUsername(String name) {
        return roleRepository.findByName(name);
    }
}
