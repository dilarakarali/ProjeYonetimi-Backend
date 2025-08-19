//Config paket: uygulama ilk çalıştığında yapılması gereken ayarları ve güvenlik kurallarını içerir.
// Uygulama ayağa kalkarken sadece bir kez çalışır. Görevi, veritabanını kontrol edip temel verilerin yerinde olduğundan emin olmaktır.
//"ROLE_ADMIN" ve "ROLE_EMPLOYEE" adında rollerin olup olmadığını kontrol eder, yoksa oluşturur.
//"admin" adında bir kullanıcı olup olmadığını kontrol eder, yoksa bu kullanıcıyı oluşturur ve ona "ROLE_ADMIN" rolünü atar.
// Bu sayede sisteme ilk giriş yapacak bir yönetici her zaman olur.

package com.kolaysoft.project_management.config;

import com.kolaysoft.project_management.entity.Employee;
import com.kolaysoft.project_management.entity.Role;
import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN", true)));

        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_EMPLOYEE", false)));

        if (employeeRepository.findByUsername("admin").isEmpty()) {
            Employee admin = new Employee();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@company.com");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole)); // Admin rolü ata

            employeeRepository.save(admin);
            System.out.println("✅ Admin oluşturuldu: kullanıcı adı = admin, şifre = admin123");
            //29 Temmuzda çıktı da oldu yani admin kullanıcısı artık var o yüzden tekrar çıktı da görmemek normal
            //çünkü zaten adminin var bi daha oluşturmaya gerek yok bu outputa da gerek yok
        }
    }
}
