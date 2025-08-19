//projects tablosunu temsil eder. Bir projenin adı, bütçesi gibi bilgilerini ve o projede hangi çalışanların olduğunu tutar.

package com.kolaysoft.project_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;  //daha hassas işlemlerde
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id yi otomatik olarak insert etsin
    private Long id;

    private String projectName;
    private String status;
    private BigDecimal budget;
    private LocalDate startDate;
    private LocalDate endDate;

    //ilk işaretlediğin yer ilişkinin sahibi
    //ilişkiyi yönetir veriyi alıp işler
    //Manytomany ve onetomany ilişkileri yeni 3.bir tablo oluşturarak gösterir
    //Onetoone ve manytoone ek bir kolon açarak ilişkilendirir
    @ManyToMany
    @JoinTable( //3.bir tablo oluştur anatasyonu
            name = "project_employees",
            joinColumns = @JoinColumn(name = "project_id"), //çalıştığım classın
            inverseJoinColumns = @JoinColumn(name = "employee_id")  //ilişkili olduğu kolonun
    )
    //private Set<Employee> employees = new HashSet<>();
    //@JsonIgnore  //Sonsuz döngüyü önler ama ilişkili taraf hiç görünmez
    //private Set<Employee> employees = new HashSet<>();
    //JsonManagedReference ve JsonBackReference de sonsuz döngüyü kırar ve çift yönlü gösterir

    @JsonManagedReference  // JSON çıktısında bu taraf gösterilir
    private Set<Employee> employees = new HashSet<>();

    @Override  //bir kişiye birden fazla proje atamak için
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return id != null && id.equals(project.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
