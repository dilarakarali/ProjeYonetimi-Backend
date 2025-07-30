package com.kolaysoft.project_management.repository;

import com.kolaysoft.project_management.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;  // interfaceyi extend ederek CRUD(create,read,update,deletete) otomatik kazanır
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {  //project sınıfnda ve primark key olan veri tipi
}
//direk autowildle enjekte edeceğim
//Jpa için