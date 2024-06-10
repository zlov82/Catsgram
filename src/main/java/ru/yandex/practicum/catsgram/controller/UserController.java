package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User createNewUser(@RequestBody User newUser) {
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (isEmailIsRegistered(newUser.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        newUser.setId(getNewUserId());
        newUser.setRegistrationDate(Instant.now());

        users.put(newUser.getId(), newUser);

        return newUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (updatedUser.getEmail() == null || updatedUser.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (isEmailIsRegisteredToOtherUser(updatedUser.getEmail(), updatedUser.getId())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        User savedUser = users.get(updatedUser.getId());

        if (updatedUser.getEmail() != null) {
            savedUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getUsername() != null) {
            savedUser.setUsername(updatedUser.getUsername());
        }

        if (updatedUser.getPassword() != null) {
            savedUser.setPassword(updatedUser.getPassword());
        }

        return savedUser;
    }


    private long getNewUserId() {
        long currentMaxUserId = users.keySet()
                .stream()
                .mapToLong(qqq -> qqq)
                .max()
                .orElse(0);

        return ++currentMaxUserId;
    }

    private boolean isEmailIsRegistered(String newEmail) {
        Optional<User> userWithEmail = users.values()
                .stream()
                .filter(user -> user.getEmail().equals(newEmail))
                .findFirst();

        if (userWithEmail.isPresent()) {
            return true;
        }
        return false;
    }

    private boolean isEmailIsRegisteredToOtherUser(String email, long userId) {
        Optional<User> userWithEmail = users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .filter(user -> user.getId() != userId)
                .findFirst();

        if (userWithEmail.isPresent()) {
            return true;
        }
        return false;
    }


}
