//Projelerle ilgili iş mantığını barındırır.
//Örneğin, bir kullanıcının sadece kendi projelerini getiren findProjectsByEmployeeUsername metodu burada yer alır.

package com.kolaysoft.project_management.service;

import com.kolaysoft.project_management.DTO.ProjectDTO; // ProjectDTO eklendi
import com.kolaysoft.project_management.entity.Employee; // Employee entity eklendi
import com.kolaysoft.project_management.entity.Project;
import com.kolaysoft.project_management.repository.EmployeeRepository; // EmployeeRepository eklendi
import com.kolaysoft.project_management.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exception eklendi
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Collectors eklendi

@Service
@RequiredArgsConstructor // Bu, final alanlar için constructor'ı otomatik oluşturur.
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository; // EmployeeRepository enjekte edildi

    public Page<Project> getProjectsPage(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project updateProject(Long id, Project updatedProject) {
        return projectRepository.findById(id).map(project -> {
            project.setProjectName(updatedProject.getProjectName());
            project.setStatus(updatedProject.getStatus());
            project.setBudget(updatedProject.getBudget());
            project.setStartDate(updatedProject.getStartDate());
            project.setEndDate(updatedProject.getEndDate());
            return projectRepository.save(project);
        }).orElse(null);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    // YENİ İŞ MANTIĞI METODU: Kullanıcı adına göre atandığı projeleri getirir
    /**
     * Verilen kullanıcı adına sahip çalışanın atandığı tüm projeleri bulur ve DTO formatında döndürür.
     *
     * @param username Giriş yapmış olan kullanıcının adı.
     * @return Projelerin DTO listesi.
     */
    public List<ProjectDTO> findProjectsByEmployeeUsername(String username) {
        // 1. Kullanıcı adına göre Employee nesnesini veritabanından bul.
        // Eğer kullanıcı bulunamazsa UsernameNotFoundException fırlat.
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Bu kullanıcı adına sahip bir çalışan bulunamadı: " + username));

        // 2. Employee nesnesine bağlı olan projeleri al ve ProjectDTO'ya dönüştür.
        // Bu dönüştürme mantığı, ProjectController'daki diğer metotlarla tutarlı olmalıdır.
        return employee.getProjects().stream()
                .map(project -> {
                    // Her bir proje için o projeye atanmış çalışanların adlarını topla
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
                })
                .collect(Collectors.toList());
    }
}