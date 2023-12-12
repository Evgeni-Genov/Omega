package com.example.omega.service;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.mapper.UserMapper;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.dto.*;
import com.example.omega.service.exception.HttpBadRequestException;
import com.example.omega.service.util.SecurityUtils;
import com.example.omega.service.util.UserServiceUtil;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Creates a new user.
     *
     * @param userCreateDTO The user to be created.
     * @return userCreateDTO           The created user.
     * @throws HttpBadRequestException If the user is invalid or the username or email is already taken.
     */
    public UserCreateDTO createUser(UserCreateDTO userCreateDTO) {
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
        return userMapper.toCreateDTO(savedUser);
    }

    /**
     * Retrieve a user by their unique user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return userDTO                 The UserDTO representing the user with the specified ID.
     * @throws HttpBadRequestException If the provided userId is invalid or null
     *                                 or if the user with the specified ID is not found.
     */
    public UserDTO getUserById(Long userId) {
        log.debug("Request to get User by ID: {}", userId);
        return userMapper.toDTO(userServiceUtil.validateAndGetUser(userId));
    }

    /**
     * Delete a user by their unique user ID.
     *
     * @param userId The ID of the user to delete.
     * @throws HttpBadRequestException If the provided userId is invalid or null
     *                                 or if the user with the specified ID is not found.
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
     * @param userUpdateDTO The DTO containing updated user information.
     * @return The UserUpdateDTO containing the updated user information.
     * @throws HttpBadRequestException If the provided userId is invalid or if the user is not found.
     */
    public UserUpdateDTO partiallyUpdateUserNonCredentialInformation(UserUpdateDTO userUpdateDTO) {
        log.debug("Request to User: {}", userUpdateDTO);
        var user = userServiceUtil.validateAndGetUser(userUpdateDTO.getId());
//        use
//        return userRepository
//                .findById(userUpdateDTO.getId())
//                .map(existingUser -> {
//                    userMapper.partialUpdate(existingUser, userUpdateDTO);
//                    return existingUser;
//                })
//                .map(userRepository::save)
//                .map(userMapper::toUpdateDTO);

        
        userServiceUtil.fieldsToBeUpdated(userUpdateDTO, user);
        var updatedUser = userMapper.toEntity(userUpdateDTO);
        //TODO: maybe use currentLoggedInUser -> there could be a mismatch
        updatedUser.setLastModifiedBy(user.getUsername());
        userRepository.save(updatedUser);
        return userUpdateDTO;
    }

    /**
     * Retrieves a user based on their nameTag.
     *
     * @param nameTag The nameTag of the user to retrieve.
     * @return UserSearchDTO The User object with the specified nameTag.
     * @throws HttpBadRequestException If no user with the given nameTag is found.
     */
    public UserSearchDTO getUserByNameTag(String nameTag) {
        log.debug("Request to get user by nameTag: {}", nameTag);
        var user = userRepository.findByNameTag(nameTag);

        if (user.isEmpty()) {
            throw new HttpBadRequestException("Can't find user with nameTag: " + nameTag);
        }

        return userMapper.toUserSearchDTO(user.get());
    }

    /**
     * Enable two-step verification for a user.
     *
     * @param userId The ID of the user to enable two-step verification for.
     * @throws HttpBadRequestException If the user with the provided ID is not found.
     */
    public void enableUserTwoStepVerification(Long userId) {
        log.debug("Request to enable two-step verification for User with ID: {}", userId);
        var user = userServiceUtil.validateAndGetUser(userId);
        user.setTwoFactorAuthentication(true);
        userRepository.save(user);
        userMapper.toUserCredentialUpdateDTO(user);
    }

    /**
     * Disable two-step verification for a user.
     *
     * @param userId The ID of the user to enable two-step verification for.
     * @throws HttpBadRequestException If the user with the provided ID is not found.
     */
    public void disableUserTwoStepVerification(Long userId) {
        log.debug("Request to disable two-step verification for User with ID: {}", userId);
        var user = userServiceUtil.validateAndGetUser(userId);
        user.setTwoFactorAuthentication(false);
        userRepository.save(user);
        userMapper.toUserCredentialUpdateDTO(user);
    }

    /**
     * Change the password for a user identified by their user ID. We receive a userPasswordChangeDTO,
     * so we can get the id, the old and the new password.
     *
     * @param userPasswordChangeDTO The UserPasswordChangeDTO.
     * @return True                    If the password change was successful, false otherwise.
     * @throws HttpBadRequestException If the provided user ID is invalid, the old password is incorrect,
     *                                 or there is an issue with the password change process.
     */
    //TODO: logout user after password change, change token.
    public UserPasswordChangeDTO changePassword(UserPasswordChangeDTO userPasswordChangeDTO) {
        log.debug("Request to update password!");
        var user = userServiceUtil.validateAndGetUser(userPasswordChangeDTO.getId());

        if (!passwordEncoder.matches(userPasswordChangeDTO.getOldPassword(), user.getPassword())) {
            throw new HttpBadRequestException("Passwords don't match!");
        }

        if (passwordEncoder.matches(userPasswordChangeDTO.getOldPassword(), userPasswordChangeDTO.getNewPassword())) {
            throw new HttpBadRequestException("New Password can't be like the old one!");
        }

        user.setPassword(passwordEncoder.encode(userPasswordChangeDTO.getNewPassword()));
        userRepository.save(user);

        log.debug("Password update was successful!");
        return userMapper.toUserPasswordChangeDTO(user);
    }

    /**
     * Change the email address for a user identified by their user ID.
     *
     * @param userSecurityUpdateDTO The UserCredentialUpdateDTO.
     * @return UserCredentialUpdateDTO containing the updated user's information.
     * @throws HttpBadRequestException If the provided user ID is invalid or there is an issue with the email change process.
     */
    public UserSecurityUpdateDTO changeEmail(UserSecurityUpdateDTO userSecurityUpdateDTO) {
        log.debug("Request to update email for user with ID: {}", userSecurityUpdateDTO.getId());
        var user = userServiceUtil.validateAndGetUser(userSecurityUpdateDTO.getId());
        userServiceUtil.validateEmailNotRegistered(userSecurityUpdateDTO.getEmail());
        user.setEmail(userSecurityUpdateDTO.getEmail());
        userRepository.save(user);
        return userMapper.toUserCredentialUpdateDTO(user);
    }

    public UserSecurityUpdateDTO updateUserSecurityData(UserSecurityUpdateDTO userSecurityUpdateDTO) {
        var user = userServiceUtil.validateAndGetUser(userSecurityUpdateDTO.getId());

        if (Boolean.TRUE.equals(userServiceUtil.shouldUpdateEmail(user.getEmail(), userSecurityUpdateDTO.getEmail()))) {
            changeEmail(userSecurityUpdateDTO);
        }

        if (!Objects.equals(user.getTwoFactorAuthentication(), userSecurityUpdateDTO.getTwoFactorAuthentication())) {
            if (Boolean.FALSE.equals(user.getTwoFactorAuthentication())) {
                enableUserTwoStepVerification(userSecurityUpdateDTO.getId());
            } else {
                disableUserTwoStepVerification(userSecurityUpdateDTO.getId());
            }
        }

        return userSecurityUpdateDTO;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

}
