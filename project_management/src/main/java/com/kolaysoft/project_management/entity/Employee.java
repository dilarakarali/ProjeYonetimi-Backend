package com.kolaysoft.project_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
@Data  // Getter, Setter, toString, equals, hashCode hepsini otomatik ekler
@NoArgsConstructor  // Parametresiz constructor
@AllArgsConstructor // Tüm alanları içeren constructor (isteğe bağlı, elle de yazılabilir)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String department;
    private String status;

//    //İlişkinin sahibi değil
//    //bu yüzden mappedBy kullanarak ilişkinin sahibindeki tanımlanmış değeri buraya vermiş olursun
//    //bunun kolonu oluşmaz
//    //çift yönlü ilişkiler için geçerli bu tarafa yazmak zorunda değilsin tek yönlü de olur
//    @ManyToMany(mappedBy = "employees")
//    //private Set<Project> projects = new HashSet<>();
//
//    //@JsonIgnore  //sonsuz döngüyü kırmak için
//    //private Set<Project> projects = new HashSet<>();
//
//    @JsonBackReference  // Bu taraf JSON çıktısında gösterilmez, döngü kırılır
//    private Set<Project> projects = new HashSet<>();


    // ✅ Giriş için gereken alanlar
    private String username;
    private String password;

    // ✅ Rol bilgisi: ManyToMany ilişki ile roller atanabilir
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(  //çalışana roll atama yeri
            name = "employee_roles",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // ✅ Proje ilişkisi (zaten vardı)
    @ManyToMany(mappedBy = "employees")
    @JsonBackReference
    private Set<Project> projects = new HashSet<>();


    @Override  //aynı projeye birden fazla kişi atamak için
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return id != null && id.equals(employee.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}






//package com.kolaysoft.project_management.model;
//
//import jakarta.persistence.*; //JPA anotasyon/notasyon(bileşene ek özellik) için
//import java.util.HashSet;
//import java.util.Set;  //Collection framework içinden set ve hashset için
//
////Hibernate(ORM araçlarından bi tanesi):Java classıyla veri tabanındaki tabloyu birbrine mapler
//@Entity  //kalıcı(persistent) nesneler oluşturur bunu tanımlamazsan tablo oluşmaz
////bu anatosyanda işaretlediklerini dışarı açmıyoruz güvenlki açığı ve istemediğim ya da ihtıyacım olmayan tüm fieldlerı gösterir
////DTO:ihtyiyacımın oldugu alanı göstermek request ve resopnse DTO şeklinde olur
//@Table(name = "employees")  //sınıfın veritabanında karşılığı olan tablonun ismi
//public class Employee {
//
//    @Id  //bu alan primary key
//    @GeneratedValue(strategy = GenerationType.IDENTITY)  //id yi arttırmak için
//    private Long id;  //id alanının türü(büyük veri setleri,null olabilir,springJPA için standart)
//
//    private String firstName;
//    private String lastName;
//    private String email;
//    private String phoneNumber;
//    private String department;
//    private String status;
//
//    @ManyToMany(mappedBy = "employees")
//    private Set<Project> projects = new HashSet<>();   //Sahip/yöneten taraf Proje
//
//    public Employee() {}  //default Constructor(framework nesne yaratırken kullanılır)
//
//    //parametreli constructor
//    public Employee(String firstName, String lastName, String email, String phoneNumber, String department, String status) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.phoneNumber = phoneNumber;
//        this.department = department;
//        this.status = status;
//    }
//
//    //Lombok kütüphanesiyle get set işlemini yapabilirsin
//    //Proje-employee çoğul ilişki olduğu için tekrar eden elemanlar olmasın diye
//    // GETTER-değer almak SETTER-const. fieldsin değerini değiştirmek
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public String getFirstName() { return firstName; }  //çalışan adını verir
//    public void setFirstName(String firstName) { this.firstName = firstName; }  //ismi neyse artık değiştirir
//    public String getLastName() { return lastName; }
//    public void setLastName(String lastName) { this.lastName = lastName; }
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//    public String getPhoneNumber() { return phoneNumber; }
//    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
//    public String getDepartment() { return department; }
//    public void setDepartment(String department) { this.department = department; }
//    public String getStatus() { return status; }
//    public void setStatus(String status) { this.status = status; }
//    public Set<com.kolaysoft.project_management.model.Project> getProjects() {return projects;}
//    public void setProjects(Set<com.kolaysoft.project_management.model.Project> projects) {this.projects = projects;}
//}
