package com.example.MovieBookingApplication.DTO;

import java.util.Set;

public class LoginResponseDTO {
    private String jwtToken;
    private String refreshToken;
    private String username;
    private Set<String> roles;

    // No-args constructor (required by some frameworks)
    public LoginResponseDTO() {
    }

    // All-args constructor
    public LoginResponseDTO(String jwtToken, String refreshToken, String username, Set<String> roles) {
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.roles = roles;
    }

    // Builder constructor (private)
    private LoginResponseDTO(Builder builder) {
        this.jwtToken = builder.jwtToken;
        this.refreshToken = builder.refreshToken;
        this.username = builder.username;
        this.roles = builder.roles;
    }

    // Getters and Setters
    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // Builder class
    public static class Builder {
        private String jwtToken;
        private String refreshToken;
        private String username;
        private Set<String> roles;

        public Builder jwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }


        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public LoginResponseDTO build() {
            return new LoginResponseDTO(this);
        }
    }

    // Static builder method
    public static Builder builder() {
        return new Builder();
    }
}