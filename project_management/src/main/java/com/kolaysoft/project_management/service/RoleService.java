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

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }


}



//package com.kolaysoft.project_management.service;
//
//import com.kolaysoft.project_management.entity.Role;
//import com.kolaysoft.project_management.repository.RoleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class RoleService {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    // Tüm rolleri getir
//    public List<Role> getAllRoles() {
//        return roleRepository.findAll();
//    }
//
//    // Yeni rol ekle
//    public Role createRole(Role role) {
//        return roleRepository.save(role);
//    }
//
//    // Rolü ID ile güncelle
//    public Optional<Role> updateRole(Long id, Role roleDetails) {
//        return roleRepository.findById(id).map(role -> {
//            role.setUsername(roleDetails.getUsername());
//            role.setAdmin(roleDetails.isAdmin());
//            return roleRepository.save(role);
//        });
//    }
//
//    // ID'ye göre sil
//    public boolean deleteRole(Long id) {
//        if (roleRepository.existsById(id)) {
//            roleRepository.deleteById(id);
//            return true;
//        }
//        return false;
//    }
//}
