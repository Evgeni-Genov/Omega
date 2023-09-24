package com.example.omega.service.impl;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.mapper.UserMapper;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.UserService;
import com.example.omega.service.dto.*;
import com.example.omega.service.exception.HttpBadRequestException;
import com.example.omega.service.util.UserServiceUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserServiceUtil userServiceUtil;


    /**
     * Creates a new user.
     *
     * @param userCreateDTO The user to be created.
     * @return The created user.
     * @throws HttpBadRequestException If the user is invalid or the username or email is already taken.
     */
    @Override
    public UserCreateDTO createUser(UserCreateDTO userCreateDTO) {
        log.debug("Validating the User data!");
        var user = userMapper.toEntity(userCreateDTO);

        userServiceUtil.validateUserNotNull(user);
        userServiceUtil.validateUserNameAndPasswordNotEmpty(user);
        userServiceUtil.validateUserNameNotTaken(user.getUserName());
        userServiceUtil.validateEmailNotRegistered(user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setNameTag(userServiceUtil.generateNameTag(user.getUserName()));

        var savedUser = userRepository.save(user);
        log.debug("User created successfully: {}", user);
        return userMapper.toCreateDTO(savedUser);
    }

    /**
     * Retrieve a user by their unique user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The UserDTO representing the user with the specified ID.
     * @throws HttpBadRequestException If the provided userId is invalid or null
     *                                 or if the user with the specified ID is not found.
     */
    @Override
    public UserDTO getUserById(Long userId) {
        log.debug("Request to get User by ID: {}", userId);
        return userMapper.toDTO(userServiceUtil.validateAndGetUser(userId));
    }

    /**
     * Checks if the given username is available, meaning it is not already taken by another user.
     *
     * @param userName The username to check for availability.
     * @return {@code true} if the username is available; {@code false} if it is already taken.
     */
    @Override
    public Boolean isUserNameAvailable(String userName) {
        log.debug("UserName: {} will be checked if it's available!", userName);
        return userRepository.existsByUserName(userName);
    }

    /**
     * Checks if the given email is available, meaning it is not already registered by another user.
     *
     * @param email The email address to check for availability.
     * @return {@code true} if the email is available; {@code false} if it is already registered.
     */
    @Override
    public Boolean isEmailAvailable(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Delete a user by their unique user ID.
     *
     * @param userId The ID of the user to delete.
     * @throws HttpBadRequestException If the provided userId is invalid or null
     *                                 or if the user with the specified ID is not found.
     */
    @Override
    public void deleteById(Long userId) {
        log.debug("Request to delete User by ID: {}", userId);
        userServiceUtil.validateAndGetUser(userId);
        userRepository.deleteById(userId);
    }

    /**
     * Retrieve the roles associated with a user by their user ID.
     *
     * @param userId The ID of the user whose roles are to be retrieved.
     * @return A list of roles associated with the user.
     */
    @Override
    public List<Roles> getRolesByUserId(Long userId) {
        log.debug("Request to get roles for User with ID: {}", userId);
        return userServiceUtil.validateAndGetUser(userId).getRoles();
    }

    /**
     * Retrieve a page of all users.
     *
     * @param pageable Pagination information to control the size and page of the result.
     * @return A page of UserDTOs containing user information.
     */
    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.debug("Request to get all Users");
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    /**
     * Updates user information based on the provided UserUpdateDTO.
     *
     * @param userId        The unique identifier of the user to be updated.
     * @param userUpdateDTO The DTO containing updated user information.
     * @return The UserUpdateDTO       containing the updated user information.
     * @throws HttpBadRequestException If the provided userId is invalid or if the user is not found.
     */
    @Override
    public UserUpdateDTO updateUserNonCredentialInformation(Long userId, UserUpdateDTO userUpdateDTO) {
        log.debug("Request to User: {}", userUpdateDTO);
        userServiceUtil.validateAndGetUser(userId);
        userRepository.save(userMapper.toEntity(userUpdateDTO));
        return userUpdateDTO;
    }

    /**
     * Retrieves a user based on their nameTag.
     *
     * @param nameTag                  The nameTag of the user to retrieve.
     * @return UserSearchDTO           The User object with the specified nameTag.
     * @throws HttpBadRequestException If no user with the given nameTag is found.
     */
    @Override
    public UserSearchDTO getUserByNameTag(String nameTag) {
        log.debug("Request to get user by nameTag: {}", nameTag);
        var user = userRepository.findByNameTag(nameTag);

        if (user.isEmpty()){
            throw new HttpBadRequestException("Can't find user with nameTag: " + nameTag);
        }

        return userMapper.toUserSearchDTO(user.get());
    }

    /**
     * Enable two-step verification for a user.
     *
     * @param  userId                  The ID of the user to enable two-step verification for.
     * @return UserDTO                 The updated UserDTO with two-step verification enabled.
     * @throws HttpBadRequestException If the user with the provided ID is not found.
     */
    @Override
    public UserCredentialUpdateDTO enableUserTwoStepVerification(Long userId) {
        log.debug("Request to enable two-step verification for User with ID: {}", userId);
        var user = userServiceUtil.validateAndGetUser(userId);
        user.setTwoFactorAuthentication(true);
        userRepository.save(user);
        return userMapper.toUserCredentialUpdateDTO(user);
    }

    /**
     * Disable two-step verification for a user.
     *
     * @param  userId                  The ID of the user to enable two-step verification for.
     * @return UserDTO                 The updated UserDTO with two-step verification enabled.
     * @throws HttpBadRequestException If the user with the provided ID is not found.
     */
    @Override
    public UserCredentialUpdateDTO disableUserTwoStepVerification(Long userId) {
        log.debug("Request to disable two-step verification for User with ID: {}", userId);
        var user = userServiceUtil.validateAndGetUser(userId);
        user.setTwoFactorAuthentication(false);
        userRepository.save(user);
        return userMapper.toUserCredentialUpdateDTO(user);
    }

    /**
     * Change the password for a user identified by their user ID.
     *
     * @param userId                   The ID of the user whose password needs to be changed.
     * @param oldPassword              The old password for verification.
     * @param newPassword              The new password to set.
     * @return True                    If the password change was successful, false otherwise.
     * @throws HttpBadRequestException If the provided user ID is invalid, the old password is incorrect,
     *                                 or there is an issue with the password change process.
     */
    @Override
    public UserCredentialUpdateDTO changePassword(Long userId, String oldPassword, String newPassword) {
        log.debug("Request to update password!");
        var user = userServiceUtil.validateAndGetUser(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new HttpBadRequestException("Passwords don't match!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.debug("Password update was successful!");
        return userMapper.toUserCredentialUpdateDTO(user);
    }

    /**
     * Change the email address for a user identified by their user ID.
     *
     * @param userId                   The ID of the user whose email needs to be changed.
     * @param newEmail                 The new email address to set.
     * @return UserCredentialUpdateDTO containing the updated user's information.
     * @throws HttpBadRequestException If the provided user ID is invalid or there is an issue with the email change process.
     */
    @Override
    public UserCredentialUpdateDTO changeEmail(Long userId, String newEmail) {
        log.debug("Request to update email for user with ID: {}", userId);
        var user = userServiceUtil.validateAndGetUser(userId);
        userServiceUtil.validateEmailNotRegistered(newEmail);
        user.setEmail(newEmail);
        userRepository.save(user);
        return userMapper.toUserCredentialUpdateDTO(user);
    }

    //! methods involving 2FA, password change and email change should be called here?
    @Override
    public UserCredentialUpdateDTO updateUserCredentials(User user) {
        return null;
    }

    //TODO: implement
    @Override
    public User authenticateUserByEmail(String email, String password) {
        return null;
    }

}
