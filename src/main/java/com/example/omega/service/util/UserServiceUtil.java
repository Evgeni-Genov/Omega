package com.example.omega.service.util;

import com.example.omega.domain.User;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.exception.BadRequestException;
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
     * Validates that the user is not null.
     *
     * @param user The user to be validated.
     * @throws BadRequestException If the user is null.
     */
    public void validateUserNotNull(User user) {
        if (user == null) {
            log.debug("Can't create null as a user!");
            throw new BadRequestException("Can't create null as a user!");
        }
    }

    /**
     * Validates that the username and password are not empty.
     *
     * @param user The user to be validated.
     * @throws BadRequestException If the username or password is empty.
     */
    public void validateUsernameAndPasswordNotEmpty(User user) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            log.debug("Username and password are required.");
            throw new BadRequestException("Username and password are required.");
        }
    }

    /**
     * Validates that the username is not already taken.
     *
     * @param username The username to be validated.
     * @throws BadRequestException If the username is already taken.
     */
    public void validateUsernameNotTaken(String username) {
        if (Boolean.TRUE.equals(existsByUsername(username))) {
            log.debug("Username {} is already taken.", username);
            throw new BadRequestException("Username is already taken.");
        }
    }

    /**
     * Validates that the email is not already registered.
     *
     * @param email The email to be validated.
     * @throws BadRequestException If the email is already registered.
     */
    public void validateEmailNotRegistered(String email) {
        if (email != null && Boolean.TRUE.equals(existsByEmail(email))) {
            log.debug("Email {} is already registered.", email);
            throw new BadRequestException("Email is already registered.");
        }
    }

    /**
     * Checks if the given email is available, meaning it is not already registered by another user.
     *
     * @param email The email address to check for availability.
     * @return {@code true} if the email is available; {@code false} if it is already registered.
     */
    public Boolean existsByEmail(String email) {
        log.debug("Email: {} will be checked if it's already registered!", email);
        return userRepository.existsByEmail(email);
    }

    /**
     * Checks if the given username is available, meaning it is not already taken by another user.
     *
     * @param username The username to check for availability.
     * @return {@code true} if the username is available; {@code false} if it is already taken.
     */
    public Boolean existsByUsername(String username) {
        log.debug("UserName: {} will be checked if it's available!", username);
        return userRepository.existsByUsername(username);
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
     * @throws BadRequestException If the provided userId is invalid
     *                                 or if the user with the specified ID is not found.
     */
    public User validateAndGetUser(Long userId) {
        log.debug("Validating and retrieving User by ID: {}", userId);

        if (userId == null) {
            throw new BadRequestException("Invalid userId provided.");
        }

        var optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new BadRequestException("User not found for ID: " + userId);
        }

        return optionalUser.get();
    }

    /**
     * Update user entity fields based on the provided UserUpdateDTO. Only non-null fields in the UserUpdateDTO
     * will be applied to the user entity.
     *
     * @param userDTO The UserUpdateDTO containing fields to update.
     * @param user          The User entity to be updated.
     */

    //TODO: StringUtils.isNotBlank()
    // checks doc, better than this.
    public void fieldsToBeUpdated(UserDTO userDTO, User user) {
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getAddress() != null) {
            user.setAddress(userDTO.getAddress());
        }
        if (userDTO.getTownOfBirth() != null) {
            user.setTownOfBirth(userDTO.getTownOfBirth());
        }
        if (userDTO.getCountryOfBirth() != null) {
            user.setCountryOfBirth(userDTO.getCountryOfBirth());
        }
        if (userDTO.getNameTag() != null) {
            user.setNameTag(userDTO.getNameTag());
        }
    }
}
