//Çalışanlarla ilgili tüm işlemleri CRUD (Listeleme, Ekleme, Güncelleme, Silme) yönetir.
// Bu endpoint'lere sadece adminler erişebilir.

package com.kolaysoft.project_management.controller;

import com.kolaysoft.project_management.DTO.EmployeeDTO;
import com.kolaysoft.project_management.entity.Employee;
import com.kolaysoft.project_management.entity.Project;
import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    //private EmployeeRepository employeeRepository;
    private EmployeeService employeeService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/paged")
    public Page<EmployeeDTO> getEmployeesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);

        return employeePage.map(employee -> {
            List<String> projectNames = employee.getProjects().stream()
                    .map(Project::getProjectName)
                    .collect(Collectors.toList());

            return new EmployeeDTO(
                    employee.getId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    employee.getPhoneNumber(),
                    employee.getDepartment(),
                    employee.getStatus(),
                    projectNames
            );
        });
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream().map(employee -> {
            List<String> projectNames = employee.getProjects().stream()
                    .map(Project::getProjectName)
                    .collect(Collectors.toList());

            return new EmployeeDTO(
                    employee.getId(),
                    employee.getFirstName(),
                    employee.getLastName(),
                    employee.getEmail(),
                    employee.getPhoneNumber(),
                    employee.getDepartment(),
                    employee.getStatus(),
                    projectNames
            );
        }).collect(Collectors.toList());
    }

    @PostMapping
    public Employee createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setStatus(employeeDTO.getStatus());

        employee.setUsername(
                (employeeDTO.getFirstName() + "." + employeeDTO.getLastName()).toLowerCase()
        );

        employee.setPassword(passwordEncoder.encode("admin123"));

        return employeeService.createEmployee(employee);
        //return employeeRepository.save(employee);
    }
    @Autowired
    private EmployeeRepository employeeRepository; // Update için geçici olarak kalsın

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeDTO details) {
        Optional<Employee> optEmployee = employeeRepository.findById(id);
        if (optEmployee.isPresent()) {
            Employee employee = optEmployee.get();
            employee.setFirstName(details.getFirstName());
            employee.setLastName(details.getLastName());
            employee.setEmail(details.getEmail());
            employee.setPhoneNumber(details.getPhoneNumber());
            employee.setDepartment(details.getDepartment());
            employee.setStatus(details.getStatus());
            Employee updatedEmployee = employeeRepository.save(employee);
            return ResponseEntity.ok(updatedEmployee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            employeeRepository.delete(employee.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
