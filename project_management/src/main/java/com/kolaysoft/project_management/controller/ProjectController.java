package com.kolaysoft.project_management.controller;

//kullanılan diğer classların dahil edilmesi
import com.kolaysoft.project_management.entity.Project;  //veri tutan model
import com.kolaysoft.project_management.repository.ProjectRepository; //veritabanı işlemi için spring data jpa interface
import org.springframework.beans.factory.annotation.Autowired; //sprinten otomatik dependency injection MVC ler birbirlerine bununla erişir
import org.springframework.http.ResponseEntity;   //HTTP cevap kontrolü
import org.springframework.web.bind.annotation.*;  //istekleri karşılar
import java.util.List;  //birden fazla proje tutmak için

//tek bir nesne tanımlayıp onu spring contexte koyup bazı yöntemlerler classlar bu veriyi çekip kendi funct. içinde kullanılırlar bunlara da BEAN denir
//Katmanlı mimari(MVC):Controller(istek attığında bu karşılar), service(veri kontrollerini yapar), repository(DB-verileri tutar- katmanından istelinilen verileri bulur)
// anatasyonları sayseninde manual bean anatasyonu oluşturmaya gerek kalmaz
//newlemek o classın içindeki her şey gier ö yüzden bi kere newleyip contexte atıp lazım olunca kullanınca bi daha newe gerek yok

@RestController //web üzerinden JSON formatında veri alışverişi  BEAN anatsayonu sayesinde app konteynırına eklenmiş olur
@RequestMapping("/api/projects")  //tüm endpointlerin temel URLsi
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;  //repo. tipinde nesne bulur

    @GetMapping  //listeleme - Postman anlasın diye bu satırları koy
    public List<Project> getAllProjects() {return projectRepository.findAll();}

    @PostMapping  //ekleme
    public Project createProject(@RequestBody Project project) {return projectRepository.save(project);}

    @PutMapping("/{id}") //güncelleme
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project details) {
        return projectRepository.findById(id)  //details ile güncellenir,save ile kaydedilir
                .map(project -> {
                    project.setProjectName(details.getProjectName());
                    project.setStatus(details.getStatus());
                    project.setBudget(details.getBudget());
                    project.setStartDate(details.getStartDate());
                    project.setEndDate(details.getEndDate());
                    return ResponseEntity.ok(projectRepository.save(project));
                }).orElse(ResponseEntity.notFound().build());
        //eğer ıd ye ait proje bulunursa map bloğu çalışır
        //aranan veri yoksa otomatik olarak HTTP 404 Not Found cevabı döner
    }

    @DeleteMapping("/{id}")  //silme
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {  //postmane adres satırı üzerinden bi variable gelecek demek
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        projectRepository.delete(project);
        return ResponseEntity.noContent().<Void>build();
    }

    @GetMapping("/test")
    public String test() {
        return "ProjectController is working!";
    }
}

