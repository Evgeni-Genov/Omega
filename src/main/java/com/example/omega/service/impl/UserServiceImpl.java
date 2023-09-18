package com.example.omega.service.impl;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.mapper.UserMapper;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.UserService;
import com.example.omega.service.dto.UserCreateDTO;
import com.example.omega.service.dto.UserCredentialUpdateDTO;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.dto.UserUpdateDTO;
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
    or if the user with the specified ID is not found.
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

    //TODO: implement
    @Override
    public UserUpdateDTO updateUser(User user) {
        return null;
    }

    @Override
    public UserCredentialUpdateDTO updateUserCredentials(User user) {
        return null;
    }

    //TODO: implement
    @Override
    public User getUserByUserName(String userName) {
        return null;
    }

    //TODO: implement
    @Override
    public User getUserByNameTag(String nameTag) {
        return null;
    }

    //TODO: implement
    @Override
    public User authenticateUserByUserName(String userName, String password) {
        return null;
    }

    //TODO: implement
    @Override
    public User authenticateUserByEmail(String email, String password) {
        return null;
    }

    //TODO: implement
    @Override
    public Boolean changePassword(String userName, String oldPassword, String newPassword) {
        return true;
    }

}
