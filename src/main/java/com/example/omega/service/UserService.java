package com.example.omega.service;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.mapper.UserMapper;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.exception.BadRequestException;
import com.example.omega.service.util.UserServiceUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;


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

    /**
     * Creates a new user.
     *
     * @param userCreateDTO The user to be created.
     * @return userCreateDTO           The created user.
     * @throws BadRequestException If the user is invalid or the username or email is already taken.
     */
    //TODO: email code verification
    public UserDTO createUser(UserDTO userCreateDTO) {
        log.debug("Validating the User data!");
        var user = userMapper.toEntity(userCreateDTO);

        userServiceUtil.validateUserNotNull(user);
        userServiceUtil.validateUsernameAndPasswordNotEmpty(user);
        userServiceUtil.validateUsernameNotTaken(user.getUsername());
        userServiceUtil.validateEmailNotRegistered(user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setNameTag(userServiceUtil.generateNameTag(user.getUsername()));
        user.setRole(Roles.ROLE_USER);
        user.setLocked(false);

        var savedUser = userRepository.save(user);
        log.debug("User created successfully: {}", user);
        return userMapper.toDTO(savedUser);
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
        var updatedUser = userMapper.toEntity(userDTO);
        userRepository.save(updatedUser);
        return userDTO;
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
     * Enable two-step verification for a user.
     *
     * @param userId The ID of the user to enable two-step verification for.
     */
    public void enableUserTwoStepVerification(Long userId) {
        log.debug("Request to enable two-step verification for User with ID: {}", userId);
        var user = userServiceUtil.validateAndGetUser(userId);
        user.setTwoFactorAuthentication(true);
        userRepository.save(user);
        userMapper.toDTO(user);
    }

    /**
     * Disable two-step verification for a user.
     *
     * @param userId The ID of the user to enable two-step verification for.
     */
    public void disableUserTwoStepVerification(Long userId) {
        log.debug("Request to disable two-step verification for User with ID: {}", userId);
        var user = userServiceUtil.validateAndGetUser(userId);
        user.setTwoFactorAuthentication(false);
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

        if (passwordEncoder.matches(userDTO.getPassword(), userDTO.getNewPassword())) {
            throw new BadRequestException("New Password can't be like the old one!");
        }

        user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
        userRepository.save(user);

        log.debug("Password update was successful!");
        return userMapper.toDTO(user);
    }

    //TODO: what if we want to change only the 2FA and not the email, same goes for phone number
    // if the newEmail is empty and the email is null -> email becomes null -> NullPointerException
    public UserDTO updateUserSecurityData(UserDTO userDTO) {
        var user = userServiceUtil.validateAndGetUser(userDTO.getId());

        if (userDTO.getNewEmail() == null || userDTO.getNewEmail().isEmpty()) {
            userDTO.setNewEmail(user.getEmail());
        } else {
            changeEmail(userDTO);
        }

        if (!Objects.equals(user.getTwoFactorAuthentication(), userDTO.getTwoFactorAuthentication())) {
            if (Boolean.FALSE.equals(user.getTwoFactorAuthentication())) {
                enableUserTwoStepVerification(userDTO.getId());
            } else {
                disableUserTwoStepVerification(userDTO.getId());
            }
        }

        return userDTO;
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
     * @param optionalUser An optional containing the user for whom the verification code is generated.
     * @return The generated verification code.
     * @throws BadRequestException if the user is not found.
     */
    public String returnSavedVerificationCode(Optional<User> optionalUser) {
        if (optionalUser.isEmpty()){
            throw new BadRequestException("User not found!");
        }
        var user = optionalUser.get();
        var verificationCode = verificationCodeService.generateVerificationCode(user);

        user.setVerificationCode(verificationCode);
        userRepository.save(user);

        return verificationCode.getCode();
    }

    /**
     * Verifies the provided code against the verification code associated with the user.
     *
     * @param user The user for whom the verification code is verified.
     * @param code The code to verify.
     * @return {@code true} if the code is valid and not expired, {@code false} otherwise.
     */
    public boolean verifyCode(User user, String code) {
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
     * Change the email address for a user identified by their user ID.
     *
     * @param userDTO The UserDTO.
     */
    //TODO: When changing the email, it would be a good idea for the user
    // to enter a verification code sent to the new email and then the email will be changed.
    private void changeEmail(UserDTO userDTO) {
        log.debug("Request to update email for user with ID: {}", userDTO.getId());
        var user = userServiceUtil.validateAndGetUser(userDTO.getId());
        validateEmailChange(userDTO, user);
        user.setEmail(userDTO.getNewEmail());
        userRepository.save(user);
        userMapper.toDTO(user);
    }
}
