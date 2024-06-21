package ru.yandex.practicum.catsgram.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"email"})
public class User {
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    private String password;
    private Instant registrationDate;
}
