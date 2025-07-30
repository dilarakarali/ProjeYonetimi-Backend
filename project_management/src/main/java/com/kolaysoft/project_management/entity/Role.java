package com.kolaysoft.project_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // "ROLE_ADMIN" veya "ROLE_EMPLOYEE"private Boolean admin;  // Rolün admin rolü olup olmadığı
    @Column(nullable = false)  //bununla hatalar kalktı şifre tekrar geldi ve site açıldı
    private boolean admin;


    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Employee> employees = new HashSet<>();

    public Role(String name, boolean admin) {  // admini set et
        this.name = name;
        this.admin = admin;
    }
}


//package com.kolaysoft.project_management.entity;
//
//import jakarta.persistence.*;
//import lombok.EqualsAndHashCode;
//
//@Entity  // Role sınıfını veritabanında tabloya dönüştürür
//@Table(name = "roles")
//@EqualsAndHashCode(exclude = "employees")
//public class Role {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Otomatik artan id
//    private Long id;
//
//    private String username;  // Kullanıcı adı
//    private boolean admin;    // Admin mi değil mi
//
//    // Boş constructor – Spring için gereklidir
//    public Role() {
//    }
//
//    // Parametreli constructor – hızlı nesne oluşturmak için
//    public Role(String username, boolean admin) {
//        this.username = username;
//        this.admin = admin;
//    }
//
//    // Getter – Setter metotları
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getUsername() { return username; }
//    public void setUsername(String username) { this.username = username; }
//
//    public boolean isAdmin() { return admin; }
//    public void setAdmin(boolean admin) { this.admin = admin; }
//
//    @Override  //veritabanında ilişkiler tekrarlanmaz
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Role)) return false;
//        Role role = (Role) o;
//        return id != null && id.equals(role.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }
//
//}
