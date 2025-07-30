package com.kolaysoft.project_management.controller;

import com.kolaysoft.project_management.entity.Employee;
import com.kolaysoft.project_management.entity.Role;
import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;  // Burada tanımladık!

    @GetMapping("/admin-area")
    public ResponseEntity<String> onlyAdminAccess(@RequestParam String username) {
        Employee employee = employeeRepository.findByUsername(username).orElse(null);

        if (employee != null) {
            Set<Role> roles = employee.getRoles();
            boolean isAdmin = roles.stream()
                    .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));

            if (isAdmin) {
                return ResponseEntity.ok("Welcome Admin!");
            }
        }

        return ResponseEntity.status(403).body("Access Denied");
    }

}



//package com.kolaysoft.project_management.controller;
//
//import com.kolaysoft.project_management.entity.Role;
//import com.kolaysoft.project_management.service.RoleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/roles")  // Postman'de kullanılacak temel URL
//public class RoleController {
//
//    @Autowired
//    private RoleService roleService;
//
//    // GET – Tüm rolleri getir
//    @GetMapping
//    public List<Role> getAllRoles() {
//        return roleService.getAllRoles();
//    }
//
//    // POST – Yeni rol oluştur
//    @PostMapping
//    public Role createRole(@RequestBody Role role) {
//        return roleService.createRole(role);
//    }
//
//    // PUT – Mevcut rolü güncelle
//    @PutMapping("/{id}")
//    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role roleDetails) {
//        return roleService.updateRole(id, roleDetails)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // DELETE – Rolü sil
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
//        if (roleService.deleteRole(id)) {
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//}
