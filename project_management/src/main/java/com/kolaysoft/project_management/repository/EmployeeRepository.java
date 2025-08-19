//Veritabanı ile doğrudan iletişim kuran arayüzlerdir.
//Spring Data JPA sayesinde, içinde metot yazmadan bile temel veritabanı işlemlerini (kaydet, bul, sil vb.) yapabilirler.
//EmployeeRepository.java: employees tablosu için veritabanı işlemleri yapar. findByUsername gibi özel sorgu metotları içerir.
//ProjectRepository.java: projects tablosu için veritabanı işlemleri yapar.
//RoleRepository.java: roles tablosu için veritabanı işlemleri yapar.

package com.kolaysoft.project_management.repository;

import com.kolaysoft.project_management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUsername(String username);

}


// Spring Data JPA, CRUD metotlarını otomatik sağlar
//HQL:sınıfın ismi ve değişken isimleri kullanarak sorgular yazılır
//SQL:tablo ismi ve tablo içindeki kolon isimleri kullanarak sorgular yazılır
//@Query(value="from(Sqlde select*from) Employee" , nativeQuery=false(Sqlde true)
//List<Employee>findAllEmployees();
