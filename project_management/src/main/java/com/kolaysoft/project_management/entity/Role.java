//roles tablosunu temsil eder. Sadece rolün adını ("ROLE_ADMIN" gibi) tutar.

package com.kolaysoft.project_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private Set<Employee> employees = new HashSet<>();

    public Role(String name, boolean admin) {  // admini set et
        this.name = name;
        this.admin = admin;
    }
}
