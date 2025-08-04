package com.kolaysoft.project_management.controller;

import com.kolaysoft.project_management.entity.Employee;
import com.kolaysoft.project_management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/getAll")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @PostMapping("/save")
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    @PutMapping("/{id}")  //güncelleme
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee details) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setFirstName(details.getFirstName());
                    employee.setLastName(details.getLastName());
                    employee.setEmail(details.getEmail());
                    employee.setPhoneNumber(details.getPhoneNumber());
                    employee.setDepartment(details.getDepartment());
                    employee.setStatus(details.getStatus());
                    return ResponseEntity.ok(employeeRepository.save(employee));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")  //silme
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        employeeRepository.delete(employee);
        return ResponseEntity.noContent().<Void>build();
    }
}

