package com.kolaysoft.project_management.security;

import com.kolaysoft.project_management.repository.EmployeeRepository;
import com.kolaysoft.project_management.entity.Employee;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final EmployeeRepository employeeRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, EmployeeRepository employeeRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.employeeRepository = employeeRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Header'dan JWT token'ı al
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // Token geçerliyse
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);

                // Kullanıcı sistemde kayıtlı mı?
                Employee employee = employeeRepository.findByUsername(username).orElse(null);

                if (employee != null) {
                    // SecurityContext'e kullanıcıyı yerleştir
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            employee, null, employee.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Diğer filtrelere devam
        filterChain.doFilter(request, response);
    }
}
