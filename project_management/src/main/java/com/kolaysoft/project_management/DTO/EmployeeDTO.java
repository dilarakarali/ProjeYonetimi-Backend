//DTO paketi:Backend ve frontend arasında veri taşımak için kullanılan özel, basit sınıflardır.
//Veritabanı yapısını doğrudan dışarıya açmamak ve veri doğrulama yapmak için kullanılırlar.
//EmployeeDTO.java / ProjectDTO.java: Frontend'e çalışan veya proje listesi gönderirken ya da frontend'den yeni bir çalışan/proje bilgisi alırken kullanılır.
// @NotBlank gibi ifadelerle gelen verinin geçerli olup olmadığını kontrol eder.

package com.kolaysoft.project_management.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private Long id;

    @NotBlank(message = "İsim boş olamaz")
    @Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ]+$", message = "Lütfen geçerli bir isim giriniz (sadece harfler)")
    private String firstName;

    @NotBlank(message = "Soyisim boş olamaz")
    @Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ]+$", message = "Lütfen geçerli bir soyisim giriniz (sadece harfler)")
    private String lastName;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Lütfen geçerli bir email giriniz")
    private String email;

    @NotBlank(message = "Telefon numarası boş olamaz")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Telefon numarası 10-11 haneli ve sadece rakamlardan oluşmalı")
    private String phoneNumber;

    @NotBlank(message = "Departman boş olamaz")
    private String department;

    @NotBlank(message = "Durum boş olamaz")
    private String status;

    private List<String> projectNames;
}
