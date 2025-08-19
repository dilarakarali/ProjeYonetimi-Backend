// Rol bazlı erişimi test etmek için basit bir endpoint içerir.

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