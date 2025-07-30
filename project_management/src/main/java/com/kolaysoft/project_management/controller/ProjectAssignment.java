package com.kolaysoft.project_management.controller;

import com.kolaysoft.project_management.DTO.EmployeeDTO;
import com.kolaysoft.project_management.DTO.ProjectDTO;
import com.kolaysoft.project_management.entity.Employee;
import com.kolaysoft.project_management.entity.Project;
import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
public class ProjectAssignment {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // ✅ Çalışanı projeye ata
    @PostMapping("/assign")
    public ResponseEntity<String> assignEmployeeToProject(@RequestParam Long projectId, @RequestParam Long employeeId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (projectOpt.isPresent() && employeeOpt.isPresent()) {
            Project project = projectOpt.get();
            Employee employee = employeeOpt.get();

            Set<Employee> employees = project.getEmployees();
            employees.add(employee); // Tekrar varsa set zaten önler
            project.setEmployees(employees);

            projectRepository.save(project);
            return ResponseEntity.ok("Employee assigned to project successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid project or employee ID.");
        }
    }

    // ✅ Projeye atanmış çalışanları EmployeeDTO listesi olarak döndür
    @GetMapping("/{projectId}/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesOfProject(@PathVariable Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isPresent()) {
            Set<Employee> employees = projectOpt.get().getEmployees();

            // Entity → DTO dönüşümü
            List<EmployeeDTO> employeeDTOs = employees.stream().map(employee -> {
                List<String> projectNames = employee.getProjects().stream()
                        .map(Project::getProjectName)
                        .collect(Collectors.toList());

                return new EmployeeDTO(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        //employee.getPhoneNumber(),
                        //employee.getDepartment(),
                        //employee.getStatus(),
                        projectNames
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(employeeDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Çalışanı projeden çıkar
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeEmployeeFromProject(@RequestParam Long projectId, @RequestParam Long employeeId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (projectOpt.isPresent() && employeeOpt.isPresent()) {
            Project project = projectOpt.get();
            Employee employee = employeeOpt.get();

            Set<Employee> employees = project.getEmployees();
            if (employees.contains(employee)) {
                employees.remove(employee);
                project.setEmployees(employees);
                projectRepository.save(project);
                return ResponseEntity.ok("Employee removed from project.");
            } else {
                return ResponseEntity.badRequest().body("Employee is not assigned to this project.");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid project or employee ID.");
        }
    }

    // ✅ Çalışana atanmış projeleri ProjectDTO olarak döndür
    @GetMapping("/employee/{employeeId}/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectsOfEmployee(@PathVariable Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (employeeOpt.isPresent()) {
            Set<Project> projects = employeeOpt.get().getProjects();

            List<ProjectDTO> projectDTOs = projects.stream().map(project -> {
                List<String> employeeNames = project.getEmployees().stream()
                        .map(emp -> emp.getFirstName() + " " + emp.getLastName())
                        .collect(Collectors.toList());

                return new ProjectDTO(
                        project.getId(),
                        project.getProjectName(),
                        project.getStatus(),
                        //project.getBudget(),
                        //project.getStartDate(),
                        //project.getEndDate(),
                        employeeNames
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(projectDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
