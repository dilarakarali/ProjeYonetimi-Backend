package com.kolaysoft.project_management.repository;

import com.kolaysoft.project_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {


    Optional<Role> findByName(String roleAdmin);
}


// Spring Data JPA, CRUD metotlarını otomatik sağlar
//HQL:sınıfın ismi ve değişken isimleri kullanarak sorgular yazılır
//SQL:tablo ismi ve tablo içindeki kolon isimleri kullanarak sorgular yazılır
//@Query(value="from(Sqlde select*from) Employee" , nativeQuery=false(Sqlde true)
//List<Employee>findAllEmployees();

