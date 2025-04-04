package com.human.tapMMO.model.tables;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private long id;

    @NotNull(message = "Email не может быть пустым.")
    @Email
    @Column(name = "email", updatable = false, unique = true, nullable = false)
    private String email;

    @NotNull(message = "Username не может быть пустым.")
    @Size(max = 32)
    @Column(name = "username", updatable = false, unique = true, nullable = false)
    private String username;

    @NotNull(message = "Password не может быть пустым.")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Pattern(regexp = "ROLE_user|ROLE_admin")
    private String role = "ROLE_user";

    @Column(name = "account_state", nullable = false)
    @Pattern(regexp = "default|baned|muted")
    private String accountState = "default";

    @Column(name = "register_date", nullable = false, updatable = false)
    private LocalDateTime registerDate = LocalDateTime.now();
}