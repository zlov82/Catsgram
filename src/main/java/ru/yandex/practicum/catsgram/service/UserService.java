package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public User createNewUser(User newUser) {
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

    public User updateUser(User updatedUser) {
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

    public Optional<User> findUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
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
