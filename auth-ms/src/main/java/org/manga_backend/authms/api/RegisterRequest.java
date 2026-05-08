package org.manga_backend.authms.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username can only contain letters, numbers, dots, hyphens, and underscores."
    )
    private String username;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    private String password;

    private String role;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
