package com.example.omega.service.util;

import com.example.omega.domain.User;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.exception.HttpBadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Helper class which holds helper methods for the UserServiceImpl class
 */
@Component
@Slf4j
@AllArgsConstructor
public class UserServiceUtil {

    private final UserRepository userRepository;

    /**
     * Checks if the given email is available, meaning it is not already registered by another user.
     *
     * @param email The email address to check for availability.
     * @return {@code true} if the email is available; {@code false} if it is already registered.
     */
    public Boolean isEmailAvailable(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks if the given username is available, meaning it is not already taken by another user.
     *
     * @param userName The username to check for availability.
     * @return {@code true} if the username is available; {@code false} if it is already taken.
     */
    public Boolean isUserNameAvailable(String userName) {
        log.debug("UserName: {} will be checked if it's available!", userName);
        return userRepository.existsByUsername(userName);
    }

    /**
     * Validates that the user is not null.
     *
     * @param user The user to be validated.
     * @throws HttpBadRequestException If the user is null.
     */
    public void validateUserNotNull(User user) {
        if (user == null) {
            log.debug("Can't create null as a user!");
            throw new HttpBadRequestException("Can't create null as a user!");
        }
    }

    /**
     * Validates that the username and password are not empty.
     *
     * @param user The user to be validated.
     * @throws HttpBadRequestException If the username or password is empty.
     */
    public void validateUserNameAndPasswordNotEmpty(User user) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            log.debug("Username and password are required.");
            throw new HttpBadRequestException("Username and password are required.");
        }
    }

    /**
     * Validates that the username is not already taken.
     *
     * @param userName The username to be validated.
     * @throws HttpBadRequestException If the username is already taken.
     */
    public void validateUserNameNotTaken(String userName) {
        if (Boolean.FALSE.equals(isUserNameAvailable(userName))) {
            log.debug("Username {} is already taken.", userName);
            throw new HttpBadRequestException("Username is already taken.");
        }
    }

    /**
     * Validates that the email is not already registered.
     *
     * @param email The email to be validated.
     * @throws HttpBadRequestException If the email is already registered.
     */
    public void validateEmailNotRegistered(String email) {
        if (Boolean.FALSE.equals(isEmailAvailable(email))) {
            log.debug("Email {} is already registered.", email);
            throw new HttpBadRequestException("Email is already registered.");
        }
    }

    /**
     * Generates a unique name tag for a user by appending a random alphanumeric and symbol string
     * to the provided username.
     *
     * @param username The username for which to generate the name tag.
     * @return A unique name tag consisting of the original username followed by a random alphanumeric
     * and symbol string.
     */
    public String generateNameTag(String username) {
        // Generate a random string of the specified length with alphanumeric and symbol characters
        log.debug("Generating the name tag of the user!");
        var randomString = RandomStringUtils.random(6, true, true);
        return username + randomString;
    }

    /**
     * Validates whether the provided userId is valid (not null and greater than zero) and checks if the user exists.
     *
     * @param userId The ID of the user to validate.
     * @return The User entity if valid and found.
     * @throws HttpBadRequestException If the provided userId is invalid
     *                                 or if the user with the specified ID is not found.
     */
    public User validateAndGetUser(Long userId) {
        log.debug("Validating and retrieving User by ID: {}", userId);

        if (userId == null) {
            throw new HttpBadRequestException("Invalid userId provided.");
        }

        var optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new HttpBadRequestException("User not found for ID: " + userId);
        }

        return optionalUser.get();
    }
}
