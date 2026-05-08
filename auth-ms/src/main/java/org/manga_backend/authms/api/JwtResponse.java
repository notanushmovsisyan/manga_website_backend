package org.manga_backend.authms.api;

public class JwtResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String role;

    public JwtResponse(String token,
                       String username,
                       String role,
                       String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
