package com.kolaysoft.project_management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data  // Getter, Setter, toString, equals, hashCode otomatik ekler
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {  //sadece göstermek istediğin alanları tanımla

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    //private String phoneNumber;
    //private String department;
    //private String status;

    // Bu çalışan hangi projelerde çalışıyor?
    private List<String> projectNames;
}
