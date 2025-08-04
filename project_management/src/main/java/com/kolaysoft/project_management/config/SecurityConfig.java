package com.kolaysoft.project_management.config;

import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.security.JwtAuthenticationFilter;
import com.kolaysoft.project_management.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private final EmployeeRepository employeeRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(EmployeeRepository employeeRepository,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.employeeRepository = employeeRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        //parolarının güvenli bi şekilde saklanmasını sağlayan şifreleme kütüphanesi
        //hash fonks. kullanarak şifreler
        //farklı bi çıktı oluşturur bu sayede parolanın kendisi saklanamz ve doğrulama işleminde kullanılmaz

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // login için açık
                        .requestMatchers("/api/projects/**").hasRole("ADMIN")
                        .requestMatchers("/api/employees/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/assignments/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                ) ;
                //.httpBasic(withDefaults());  401 hatası aldığım için bunu yorum satırına aldım o hata çözüldü

        // JWT filtremizi ekliyoruz
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

