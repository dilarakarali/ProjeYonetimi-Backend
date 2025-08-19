//Backend'den frontend'e başarılı giriş sonrası kullanıcı adı, token ve rolleri taşır.

package com.kolaysoft.project_management.DTO;

import java.util.List;

public class LoginResponse {
    private String username;
    private String token;
    private List<String> roles;

    public LoginResponse() {
    }

    public LoginResponse(String username, String token, List<String> roles) {
        this.username = username;
        this.token = token;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}