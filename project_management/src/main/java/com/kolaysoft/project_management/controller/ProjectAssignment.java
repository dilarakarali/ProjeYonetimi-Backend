//Projeler ve çalışanlar arasındaki ilişkiyi yönetir.
//Bir çalışanı bir projeye atama (/assign) işlemini yapar.
//Bir çalışanı bir projeden çıkarma (/remove) işlemini yapar.
//Giriş yapmış bir çalışanın kendi projelerini görmesini sağlayan (/my-projects) endpoint'i barındırır.

package com.kolaysoft.project_management.controller;

import com.kolaysoft.project_management.DTO.EmployeeDTO;
import com.kolaysoft.project_management.DTO.ProjectDTO;
import com.kolaysoft.project_management.entity.Employee;
import com.kolaysoft.project_management.entity.Project;
import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.repository.ProjectRepository;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/assign")
    public ResponseEntity<String> assignEmployeeToProject(@RequestParam Long projectId, @RequestParam Long employeeId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (projectOpt.isPresent() && employeeOpt.isPresent()) {
            Project project = projectOpt.get();
            Employee employee = employeeOpt.get();

            if (project.getEmployees().contains(employee)) {
                return ResponseEntity.badRequest().body("Employee is already assigned to this project.");
            }

            project.getEmployees().add(employee);
            employee.getProjects().add(project);

            projectRepository.save(project);
            employeeRepository.save(employee);

            return ResponseEntity.ok("Employee assigned to project successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid project or employee ID.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, List<EmployeeDTO>>> getAllAssignments() {
        List<Project> projects = projectRepository.findAll();

        Map<String, List<EmployeeDTO>> result = new HashMap<>();

        for (Project project : projects) {
            String projectName = project.getProjectName();

            List<EmployeeDTO> employeeDTOs = project.getEmployees().stream().map(employee -> {
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

            result.put(projectName, employeeDTOs);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{projectId}/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByProjectId(@PathVariable Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            List<EmployeeDTO> employeeDTOs = project.getEmployees().stream().map(employee -> {
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

            return ResponseEntity.ok(employeeDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeEmployeeFromProject(@RequestParam Long projectId, @RequestParam Long employeeId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (projectOpt.isPresent() && employeeOpt.isPresent()) {
            Project project = projectOpt.get();
            Employee employee = employeeOpt.get();

            if (project.getEmployees().contains(employee)) {
                project.getEmployees().remove(employee);
                employee.getProjects().remove(project);  // İlişkiyi karşılıklı kaldır

                projectRepository.save(project);
                employeeRepository.save(employee);

                return ResponseEntity.ok("Employee removed from project.");
            } else {
                return ResponseEntity.badRequest().body("Employee is not assigned to this project.");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid project or employee ID.");
        }
    }

    // ✅ Çalışanın sadece kendi projelerini görmesi
    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectDTO>> getMyProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // login olan kullanıcının username’i

        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);

        if (employeeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Employee employee = employeeOpt.get();
        Set<Project> projects = employee.getProjects();

        List<ProjectDTO> projectDTOs = projects.stream().map(project -> {
            List<String> employeeNames = project.getEmployees().stream()
                    .map(emp -> emp.getFirstName() + " " + emp.getLastName())
                    .collect(Collectors.toList());

            return new ProjectDTO(
                    project.getId(),
                    project.getProjectName(),
                    project.getStatus(),
                    project.getBudget(),
                    project.getStartDate(),
                    project.getEndDate(),
                    employeeNames
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(projectDTOs);
    }



    // Çalışana atanmış projeleri ProjectDTO olarak döndür
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
                        project.getBudget(),
                        project.getStartDate(),
                        project.getEndDate(),
                        employeeNames
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(projectDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
