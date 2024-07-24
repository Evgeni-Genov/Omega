package com.example.omega.service;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.mapper.UserMapper;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.exception.BadRequestException;
import com.example.omega.service.util.PasswordResetLinkService;
import com.example.omega.service.util.UserServiceUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.example.omega.service.util.Constants.USER_PROFILE_DIR;


@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserServiceUtil userServiceUtil;
    private final VerificationCodeService verificationCodeService;
    private final PasswordResetLinkService passwordResetLinkService;

    /**
     * Creates a new user.
     *
     * @param userCreateDTO The user to be created.
     * @return userCreateDTO           The created user.
     * @throws BadRequestException If the user is invalid or the username or email is already taken.
     */
    public UserDTO createUser(UserDTO userCreateDTO) {
        log.debug("Validating the User data!");
        var user = userMapper.toEntity(userCreateDTO);

        userServiceUtil.validateUserNotNull(user);
        userServiceUtil.validateEmailNotRegistered(user.getEmail());
        userServiceUtil.validateUsernameAndPasswordNotEmpty(user);
        userServiceUtil.validateUsernameNotTaken(user.getUsername());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setNameTag(userServiceUtil.generateNameTag(user.getUsername()));
        user.setRole(Roles.ROLE_USER);
        user.setLocked(false);
        user.setEnabled(false);

        var savedUser = userRepository.save(user);
        log.debug("User created successfully: {}", user);
        return userMapper.toDTO(savedUser);
    }

    /**
     * Checks if a user is enabled based on their username.
     *
     * @param username The username of the user to check.
     * @return boolean True if the user is enabled, false otherwise.
     * @throws BadRequestException if no user is found with the given username.
     */
    public boolean isUserEnabled(String username) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new BadRequestException(String.format("User with username: %s is not found", username));
        }

        return user.get().getEnabled();
    }

    /**
     * Activates a user account.
     *
     * @param userDTO The UserDTO containing the user information to activate.
     */
    public void activateUser(UserDTO userDTO) {
        userDTO.setEnabled(true);
        userRepository.save(userMapper.toEntity(userDTO));
        log.debug("User activated successfully: {}", userDTO);
    }

    /**
     * Finds a user by their email verification token.
     *
     * @param token The email verification token to search for.
     * @return UserDTO The user information associated with the given token.
     * @throws BadRequestException if no user is found with the given token.
     */
    public UserDTO findUserByEmailVerificationToken(String token) {
        log.debug("Find User by email verification token: {}", token);
        var user = userRepository.getUserByEmailVerificationTokenEquals(token);
        if (user.isEmpty()) {
            throw new BadRequestException("Invalid verification token");
        }

        return userMapper.toDTO(user.get());
    }

    /**
     * Retrieve a user by their unique user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return userDTO                 The UserDTO representing the user with the specified ID.
     * @throws BadRequestException If the provided userId is invalid or null
     *                             or if the user with the specified ID is not found.
     */
    public UserDTO getUserById(Long userId) {
        log.debug("Request to get User by ID: {}", userId);
        return userMapper.toDTO(userServiceUtil.validateAndGetUser(userId));
    }

    /**
     * Delete a user by their unique user ID.
     *
     * @param userId The ID of the user to delete.
     * @throws BadRequestException If the provided userId is invalid or null
     *                             or if the user with the specified ID is not found.
     */
    public void deleteById(Long userId) {
        log.debug("Request to delete User by ID: {}", userId);
        userServiceUtil.validateAndGetUser(userId);
        userRepository.deleteById(userId);
    }

    /**
     * Retrieve a page of all users.
     *
     * @param pageable Pagination information to control the size and page of the result.
     * @return A page of UserDTOs containing user information.
     */
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.debug("Request to get all Users");
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    /**
     * Updates user information based on the provided UserUpdateDTO.
     *
     * @param userDTO The DTO containing updated user information.
     * @return The UserUpdateDTO containing the updated user information.
     * @throws BadRequestException If the provided userId is invalid or if the user is not found.
     */
    public UserDTO partiallyUpdateUserNonCredentialInformation(UserDTO userDTO) {
        log.debug("Request to User: {}", userDTO);
        var user = userServiceUtil.validateAndGetUser(userDTO.getId());
        userServiceUtil.fieldsToBeUpdated(userDTO, user);
        userMapper.updateUserFromDto(userDTO, user);
        var updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Retrieves a list of users based on their nameTag.
     *
     * @param nameTag The nameTag to search for users.
     * @return A list of UserDTO objects that contain the specified nameTag.
     * @throws BadRequestException If no users with the given nameTag are found.
     */
    public List<UserDTO> getUsersByNameTagContaining(String nameTag) {
        log.debug("Request to get users by nameTag containing: {}", nameTag);
        var users = userRepository.findByNameTagContainingIgnoreCase(nameTag);

        if (users.isEmpty()) {
            throw new BadRequestException("Can't find users with nameTag containing: " + nameTag);
        }

        return users.stream().map(userMapper::toDTO).toList();
    }

    /**
     * Retrieves a user based on their nameTag.
     *
     * @param nameTag The nameTag of the user to retrieve.
     * @return UserSearchDTO The User object with the specified nameTag.
     * @throws BadRequestException If no user with the given nameTag is found.
     */
    public UserDTO getUserByNameTag(String nameTag) {
        log.debug("Request to get user by nameTag: {}", nameTag);
        var user = userRepository.findByNameTag(nameTag);

        if (user.isEmpty()) {
            throw new BadRequestException("Can't find user with nameTag: " + nameTag);
        }

        return userMapper.toDTO(user.get());
    }

    /**
     * Updates the two-step verification setting for a user.
     *
     * @param userId                      The ID of the user whose two-step verification setting is to be updated.
     * @param twoFactorAuthenticationFlag The new state of the two-factor authentication flag.
     * @throws BadRequestException if the user with the given ID is not found.
     */
    public void updateUserTwoStepVerification(Long userId, boolean twoFactorAuthenticationFlag) {
        log.debug("Request to update two-step verification for User with ID: {} to {}", userId, twoFactorAuthenticationFlag);
        var user = userServiceUtil.validateAndGetUser(userId);
        user.setTwoFactorAuthentication(twoFactorAuthenticationFlag);
        userRepository.save(user);
        userMapper.toDTO(user);
    }

    /**
     * Change the password for a user identified by their user ID. We receive a userDTO,
     * so we can get the id, the old and the new password.
     *
     * @param userDTO The UserPasswordChangeDTO.
     * @return True                    If the password change was successful, false otherwise.
     * @throws BadRequestException If the provided user ID is invalid, the old password is incorrect,
     *                             or there is an issue with the password change process.
     */
    //TODO: logout user after password change, change token.
    public UserDTO changePassword(UserDTO userDTO) {
        log.debug("Request to update password!");
        var user = userServiceUtil.validateAndGetUser(userDTO.getId());

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("Passwords don't match!");
        }

        if (userDTO.getPassword().equals(userDTO.getNewPassword())) {
            throw new BadRequestException("New Password can't be like the old one!");
        }

        if (!userDTO.getNewPassword().equals(userDTO.getConfirmNewPassword())) {
            throw new BadRequestException("The new password and the confirm password do not match!");
        }

        user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
        userRepository.save(user);

        log.debug("Password update was successful!");
        return userMapper.toDTO(user);
    }

    /**
     * Resets the password for a given user.
     *
     * @param user    The User entity whose password is to be reset.
     * @param userDTO The UserDTO containing the new password information.
     * @return UserDTO The updated user information after password reset.
     * @throws BadRequestException if the new password and confirm password don't match.
     */
    public UserDTO passwordReset(User user, UserDTO userDTO) {
        log.debug("Request to reset password!");

        if (StringUtils.isNotBlank(userDTO.getNewPassword()) &&
                StringUtils.isNotBlank(userDTO.getConfirmNewPassword()) &&
                !userDTO.getNewPassword().equals(userDTO.getConfirmNewPassword())) {
            throw new BadRequestException("Passwords don't match!");
        }

        user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
        userRepository.save(user);

        log.debug("Password reset was successful!");
        return userMapper.toDTO(user);
    }

    /**
     * Retrieves a user with their authorities by username.
     *
     * @param username The username of the user to retrieve.
     * @return An optional containing the user with authorities if found, or an empty optional if not found.
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    /**
     * Retrieves a user by email.
     *
     * @param email The email of the user to retrieve.
     * @return An optional containing the user if found, or an empty optional if not found.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Generates a verification code for the specified user, saves it, and associates it with the user.
     *
     * @param user The user for whom the verification code is generated.
     * @return The generated verification code.
     * @throws BadRequestException if the user is not found.
     */
    @Transactional
    public String returnSavedVerificationCode(User user) {
        var verificationCode = verificationCodeService.generateVerificationCode(user);
        user.setVerificationCode(verificationCode);
        userRepository.saveAndFlush(user);
        return verificationCode.getCode();
    }

    /**
     * Generates and saves a password reset link for a given user.
     *
     * @param optionalUser An Optional containing the User for whom to generate the password reset link.
     * @return String The token of the generated password reset link.
     * @throws BadRequestException if the Optional<User> is empty (user not found).
     */
    public String returnSavedPasswordResetLink(Optional<User> optionalUser) {
        if (optionalUser.isEmpty()) {
            throw new BadRequestException("User not found!");
        }
        var user = optionalUser.get();
        var passwordResetLink = passwordResetLinkService.generatePasswordResetLink(user);

        user.setPasswordResetLink(passwordResetLink);
        userRepository.save(user);

        return passwordResetLink.getToken();
    }

    /**
     * Verifies the provided code against the verification code associated with the user.
     *
     * @param userDTO The userDTO for whom the verification code is verified.
     * @param code    The code to verify.
     * @return {@code true} if the code is valid and not expired, {@code false} otherwise.
     */
    public boolean verifyCode(UserDTO userDTO, String code) {
        var user = userServiceUtil.validateAndGetUser(userDTO.getId());
        var verificationCode = user.getVerificationCode();

        if (verificationCode != null
                && verificationCode.getCode().equals(code)
                && (!verificationCodeService.isExpired(verificationCode))) {
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    /**
     * Updates the two-factor authentication secret for a user.
     *
     * @param user      The User entity to update.
     * @param secretKey The new two-factor authentication secret key to set.
     * @throws IllegalArgumentException if user is null or secretKey is null or empty.
     */
    public void updateTwoFactorSecret(User user, String secretKey) {
        user.setTwoFactorSecret(secretKey);
        userRepository.save(user);
        log.debug("Updated Two-Factor Secret for User ID: {}", user.getId());
    }

    /**
     * Updates the email address of a user identified by their user ID.
     *
     * @param userDTO The UserDTO containing the user's ID and new email information.
     * @return The updated User entity.
     */
    public User updateUserEmailEntity(UserDTO userDTO) {
        log.debug("Request to update email for user with ID: {}", userDTO.getId());
        var user = userServiceUtil.validateAndGetUser(userDTO.getId());

        validateEmailChange(userDTO, user);
        user.setEmail(userDTO.getNewEmail());
        userRepository.save(user);

        return user;
    }

    /**
     * Updates the email address of a user and returns the updated UserDTO.
     *
     * @param userDTO The UserDTO containing the user's ID and new email information.
     * @return The updated UserDTO.
     */
    public UserDTO updateUserEmail(UserDTO userDTO) {
        var user = updateUserEmailEntity(userDTO);
        return userMapper.toDTO(user);
    }

    /**
     * Validates the change of email for a user.
     * Checks if the new email is not already registered, and if the current email matches the user's email.
     *
     * @param userDTO The DTO containing the user's new email.
     * @param user    The user entity whose email is being changed.
     * @throws BadRequestException if the new email is already registered, or if the current email doesn't match the user's email.
     */
    private void validateEmailChange(UserDTO userDTO, User user) {
        if (!user.getEmail().equals(userDTO.getEmail())) {
            throw new BadRequestException("Emails don't match!");
        }

        userServiceUtil.validateEmailNotRegistered(userDTO.getNewEmail());

        if (user.getEmail().equals(userDTO.getNewEmail())) {
            throw new BadRequestException("The new email can't be the same as the old one");
        }
    }

    /**
     * Updates the user's avatar.
     *
     * @param userId     The ID of the user whose avatar is being updated.
     * @param avatarFile The new avatar file.
     * @return UserDTO The updated user information.
     * @throws BadRequestException if the avatar file cannot be saved.
     */
    public UserDTO updateUserAvatar(Long userId, MultipartFile avatarFile) {
        var user = userServiceUtil.validateAndGetUser(userId);

        if (user.getAvatar() != null) {
            deleteAvatarFile(user.getAvatar());
        }

        var avatarUrl = saveAvatarFile(avatarFile);
        user.setAvatar(avatarUrl);
        var updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Saves the avatar file to the file system.
     *
     * @param avatarFile The avatar file to save.
     * @return String The URL of the saved avatar file.
     * @throws BadRequestException if the file cannot be saved.
     */
    private String saveAvatarFile(MultipartFile avatarFile) {
        try {
            Files.createDirectories(Paths.get(USER_PROFILE_DIR));
            var filename = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
            var filePath = Paths.get(USER_PROFILE_DIR, filename);
            Files.write(filePath, avatarFile.getBytes());
            return "userProfiles/" + filename;
        } catch (IOException e) {
            throw new BadRequestException("Failed to save avatar file");
        }
    }

    /**
     * Deletes the old avatar file from the file system.
     *
     * @param avatarUrl The URL of the old avatar file.
     * @throws BadRequestException if the file cannot be deleted.
     */
    private void deleteAvatarFile(String avatarUrl) {
        try {
            var filePath = Paths.get("src/main/resources/", avatarUrl);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new BadRequestException("Failed to delete old avatar file");
        }
    }

    /**
     * Updates the user's username.
     *
     * @param userDTO The DTO containing the user ID and new username.
     * @return UserDTO The updated user information.
     * @throws BadRequestException if the new username is empty or the same as the old username.
     */
    public UserDTO updateUsername(UserDTO userDTO) {
        var user = userServiceUtil.validateAndGetUser(userDTO.getId());

        if (StringUtils.isBlank(userDTO.getNewUsername())) {
            throw new BadRequestException("New username cannot be empty!");
        }

        if (user.getUsername().equals(userDTO.getNewUsername())) {
            throw new BadRequestException("New username cannot be like the old one!");
        }

        user.setUsername(userDTO.getNewUsername());
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    /**
     * Updates the phone number for a user.
     *
     * @param userDTO The UserDTO containing the user's ID, current phone number, and new phone number.
     * @return UserDTO The updated user information.
     * @throws BadRequestException If the new phone number is empty or if the current phone number doesn't match the existing one.
     */
    public UserDTO updatePhoneNumber(UserDTO userDTO) {
        var user = userServiceUtil.validateAndGetUser(userDTO.getId());

        if (StringUtils.isBlank(userDTO.getNewPhoneNumber())) {
            throw new BadRequestException("New phone number cannot be empty!");
        }

        if (StringUtils.isNotBlank(user.getPhoneNumber()) &&
                !user.getPhoneNumber().equals(userDTO.getPhoneNumber())) {
            throw new BadRequestException("Current phone number does not match the existing phone number!");
        }

        user.setPhoneNumber(userDTO.getNewPhoneNumber());
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    /**
     * Sets the budgeting flag for a user.
     *
     * @param userId             The ID of the user.
     * @param budgetingFlagValue The boolean value to set for the budgeting flag.
     * @throws BadRequestException If the user is not found.
     */
    public void setUserBudgeting(Long userId, boolean budgetingFlagValue) {
        var user = userServiceUtil.validateAndGetUser(userId);
        user.setIsBudgetingEnabled(budgetingFlagValue);
        userRepository.save(user);
    }

    /**
     * Retrieves the content of a user's avatar file.
     *
     * @param filename The name of the avatar file.
     * @return byte[] The content of the avatar file.
     * @throws BadRequestException If there's an error reading the avatar file.
     */
    public byte[] getAvatarContent(String filename) {
        try {
            var filePath = Paths.get(USER_PROFILE_DIR).resolve(filename).normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new BadRequestException("Error reading avatar file: " + filename);
        }
    }

    /**
     * Finds the email address associated with a given username.
     *
     * @param username The username to search for.
     * @return String The email address associated with the username.
     */
    public String findEmailByUsername(String username) {
        return userRepository.findEmailByUsername(username);
    }
}