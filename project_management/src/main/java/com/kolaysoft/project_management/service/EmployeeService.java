//Service paketi:Uygulamanın asıl iş mantığının bulunduğu yerdir.
//Controller'dan aldığı verilerle ne yapacağına karar verir ve Repository aracılığıyla veritabanı işlemlerini yönetir.
//EmployeeService.java: Yeni bir çalışan oluşturulduğunda ona otomatik olarak ROLE_EMPLOYEE atanması gibi iş kurallarını içerir.

package com.kolaysoft.project_management.service;

import com.kolaysoft.project_management.entity.Employee;
import com.kolaysoft.project_management.entity.Role;
import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    //private final PasswordEncoder passwordEncoder;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee createEmployee(Employee employee) {

//String generatedUsername = (employee.getFirstName() + "." + employee.getLastName()).toLowerCase();
//employee.setUsername(generatedUsername);
//employee.setPassword(passwordEncoder.encode("admin123"));

        Role defaultRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_EMPLOYEE", false)));

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        employee.setRoles(roles);

        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setFirstName(updatedEmployee.getFirstName());
            employee.setLastName(updatedEmployee.getLastName());
            employee.setEmail(updatedEmployee.getEmail());
            employee.setPhoneNumber(updatedEmployee.getPhoneNumber());
            employee.setDepartment(updatedEmployee.getDepartment());
            employee.setStatus(updatedEmployee.getStatus());
            return employeeRepository.save(employee);
        }).orElse(null);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
