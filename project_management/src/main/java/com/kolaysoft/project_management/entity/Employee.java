package com.kolaysoft.project_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;  // EKLENDİ

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements UserDetails {  // BURAYA EKLENDİ

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String department;
    private String status;

    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_roles",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "employees")
    @JsonBackReference
    private Set<Project> projects = new HashSet<>();


    // UserDetails metodları
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))  //403 forbidden hatası için Role yi sildim pgadmin4 te ROLEADMIN diye var zaten veri tabanımda
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {   // UserDetails zorunlu metodu
        return password;
    }

    @Override
    public String getUsername() {   // UserDetails zorunlu metodu
        return username;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }


    // equals ve hashCode metodları sınıfın içinde ve doğru yerde
    @Override
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
