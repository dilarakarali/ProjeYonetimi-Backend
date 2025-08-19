//Projelerle ilgili CRUD işlemlerini yönetir.
//Bu da adminlere özeldir.

package com.kolaysoft.project_management.controller;

import com.kolaysoft.project_management.DTO.ProjectDTO;
import com.kolaysoft.project_management.entity.Project;
import com.kolaysoft.project_management.repository.ProjectRepository;
import com.kolaysoft.project_management.service.ProjectService; // ProjectService import edildi
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Spring Security Authentication import edildi
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService; // ProjectService enjekte edildi

    // Constructor Injection (Autowired yerine daha temiz bir yaklaşım)
    public ProjectController(ProjectRepository projectRepository, ProjectService projectService) {
        this.projectRepository = projectRepository;
        this.projectService = projectService;
    }

    // ADMIN yetkisi gerektiren metotlar - SecurityConfig tarafından korunuyor
    @GetMapping
    public Page<ProjectDTO> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projectsPage = projectRepository.findAll(pageable);

        Page<ProjectDTO> dtoPage = projectsPage.map(project -> {
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
        });

        return dtoPage;
    }

    // Sayfalama olmadan, tüm projeleri döner (dropdown için) - SADECE ADMIN
    @GetMapping("/all")
    public List<ProjectDTO> getAllProjectsWithoutPagination() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream().map(project -> {
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
    }

    // Proje oluşturma - SADECE ADMIN
    @PostMapping
    public Project createProject(@RequestBody ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectName(projectDTO.getProjectName());
        project.setStatus(projectDTO.getStatus());
        project.setBudget(projectDTO.getBudget());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());

        return projectRepository.save(project);
    }

    // Proje güncelleme - SADECE ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        Optional<Project> optProject = projectRepository.findById(id);

        if (optProject.isPresent()) {
            Project project = optProject.get();
            project.setProjectName(projectDTO.getProjectName());
            project.setStatus(projectDTO.getStatus());
            project.setBudget(projectDTO.getBudget());
            project.setStartDate(projectDTO.getStartDate());
            project.setEndDate(projectDTO.getEndDate());

            Project updatedProject = projectRepository.save(project);
            return ResponseEntity.ok(updatedProject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Proje silme (Metot adı düzeltildi) - SADECE ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        projectRepository.delete(project);
        return ResponseEntity.noContent().<Void>build();
    }

    // YENİ VE GÜVENLİ ENDPOINT: Employee'nin sadece kendi projelerini görmesi için
    // Bu endpoint hem ADMIN hem de EMPLOYEE tarafından erişilebilir.
    @GetMapping("/my-assignments")
    public ResponseEntity<List<ProjectDTO>> getMyAssignedProjects(Authentication authentication) {
        // Spring Security Authentication objesinden giriş yapmış kullanıcının username'ini alıyoruz.
        String currentUsername = authentication.getName();

        // İş mantığını ProjectService katmanına devrediyoruz.
        List<ProjectDTO> projects = projectService.findProjectsByEmployeeUsername(currentUsername);

        return ResponseEntity.ok(projects);
    }
}