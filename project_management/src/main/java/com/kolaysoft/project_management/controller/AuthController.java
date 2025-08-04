package com.kolaysoft.project_management.controller;

import com.kolaysoft.project_management.DTO.LoginRequest;
import com.kolaysoft.project_management.DTO.LoginResponse;
import com.kolaysoft.project_management.entity.Employee;
import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 1. Giriş bilgilerini doğrula
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Giriş başarılıysa kullanıcıyı çek
        Employee employee = employeeRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // 3. Kullanıcının rollerini al
        List<String> roles = employee.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        // 4. Token oluştur
        String token = jwtTokenProvider.generateToken(authentication);

        // 5. Yanıtı döndür
        return ResponseEntity.ok(new LoginResponse(employee.getUsername(), token, roles));
    }
}
