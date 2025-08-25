package com.kolaysoft.project_management.config;

import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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

    // Bu Bean'ler doğru, bunlara dokunmuyoruz.
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> employeeRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // CORS ayarları da doğru, buna da dokunmuyoruz.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://projeyonetimifrontend.onrender.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // --- ASIL DEĞİŞİKLİK BURADA: GÜVENLİK FİLTRE ZİNCİRİ ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // CORS ayarlarını en başa alıyoruz.
                .csrf(AbstractHttpConfigurer::disable) // CSRF'i devre dışı bırakıyoruz.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ÖNEMLİ: API yollarının başına "/api" ekleyerek netleştiriyoruz.
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/employees/**").hasRole("ADMIN")
                        .requestMatchers("/api/projects/my-assignments").authenticated() // Sadece giriş yapmış olması yeterli
                        .requestMatchers("/api/projects/**").hasRole("ADMIN")
                        .requestMatchers("/api/assignments/**").hasRole("ADMIN")
                        // Geri kalan TÜM "/api/" istekleri için kimlik doğrulaması iste
                        .requestMatchers("/api/**").authenticated()
                        // "/api/" ile başlamayan diğer tüm isteklere (frontend yönlendirmeleri gibi) izin ver
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}



//package com.kolaysoft.project_management.config;
//
//import com.kolaysoft.project_management.repository.EmployeeRepository;
//import com.kolaysoft.project_management.security.JwtAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//@Configuration
//public class SecurityConfig {
//
//    private final EmployeeRepository employeeRepository;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig(EmployeeRepository employeeRepository,
//                          JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.employeeRepository = employeeRepository;
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> employeeRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Bu satırda, aşağıdaki corsConfigurationSource Bean'ini kullanmasını söylüyoruz
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll() // Login herkese açık
//
//                        // KURAL 1: Employee'nin sadece kendi proje atamalarını görebileceği endpoint.
//                        .requestMatchers(HttpMethod.GET, "/api/projects/my-assignments").hasAnyRole("ADMIN", "EMPLOYEE")
//
//                        // KURAL 2: Project endpoint'lerinin geri kalanı SADECE ADMIN'e özel.
//                        .requestMatchers("/api/projects/**").hasRole("ADMIN")
//
//                        // KURAL 3: Employee endpoint'lerinin tamamı SADECE ADMIN'e özel.
//                        .requestMatchers("/api/employees/**").hasRole("ADMIN")
//
//                        // Diğer tüm istekler için kimlik doğrulaması gereklidir.
//                        .anyRequest().authenticated()
//                );
//
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // İZİN VERİLEN ADRESLER LİSTESİ: Hem localhost'u hem de Render'ı ekledik!
//        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://projeyonetimifrontend.onrender.com"));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}



////Projenin güvenlik merkezidir. Kimin nereye erişebileceğini tanımlar.
////Erişim Kuralları (Authorization): Hangi URL'lere kimlerin erişebileceğini belirler.
//// Örneğin: /api/auth/** (login sayfası) herkese açıktır.
//// /api/employees/** ve /api/projects/** gibi yönetim sayfalarına sadece ROLE_ADMIN olanlar erişebilir.
//// /api/projects/my-assignments sayfasına hem ROLE_ADMIN hem de ROLE_EMPLOYEE erişebilir.
//// Kimlik Doğrulama (Authentication): Bir kullanıcının veritabanından nasıl bulunacağını (UserDetailsService) ve şifrelerin nasıl kontrol edileceğini (PasswordEncoder) tanımlar.
//// CORS Ayarları: http://localhost:5173 adresinde çalışan React uygulamasının backend'e istek atabilmesi için gerekli olan izni verir.
//// JWT Filtresini Devreye Sokar: Gelen her isteğin JwtAuthenticationFilter tarafından kontrol edilmesini sağlar.
//
//
//
//package com.kolaysoft.project_management.config;
//
//import com.kolaysoft.project_management.repository.EmployeeRepository;
//import com.kolaysoft.project_management.security.JwtAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//@Configuration
//public class SecurityConfig {
//
//    private final EmployeeRepository employeeRepository;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig(EmployeeRepository employeeRepository,
//                          JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.employeeRepository = employeeRepository;
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> employeeRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors().and()
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll() // Login herkese açık
//
//                        // KURAL 1: Employee'nin sadece kendi proje atamalarını görebileceği endpoint.
//                        // Hem ADMIN hem de EMPLOYEE erişebilir.
//                        .requestMatchers(HttpMethod.GET, "/api/projects/my-assignments").hasAnyRole("ADMIN", "EMPLOYEE")
//
//                        // KURAL 2: Project endpoint'lerinin geri kalanı SADECE ADMIN'e özel.
//                        .requestMatchers("/api/projects/**").hasRole("ADMIN")
//
//                        // KURAL 3: Employee endpoint'lerinin tamamı SADECE ADMIN'e özel.
//                        .requestMatchers("/api/employees/**").hasRole("ADMIN")
//
//                        // KURAL 4: (Varsa) Diğer hassas endpoint'ler de SADECE ADMIN'e özel olmalı.
//                        // Örnek: .requestMatchers("/api/roles/**").hasRole("ADMIN")
//
//                        // Diğer tüm istekler için kimlik doğrulaması gereklidir.
//                        .anyRequest().authenticated()
//                );
//
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
