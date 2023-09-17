package com.example.omega.service.impl;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.mapper.UserMapper;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.UserService;
import com.example.omega.service.dto.UserCreateDTO;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.exception.HttpBadRequestException;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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


    /**
     * Creates a new user.
     *
     * @param userCreateDTO The user to be created.
     * @return The created user.
     * @throws HttpBadRequestException If the user is invalid or the username or email is already taken.
     */
    @Override
    public UserCreateDTO createUser(UserCreateDTO userCreateDTO) {
        var user = userMapper.toEntity(userCreateDTO);

        validateUserNotNull(user);
        validateUserNameAndPasswordNotEmpty(user);
        validateUserNameNotTaken(user.getUserName());
        validateEmailNotRegistered(user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setNameTag(generateNameTag(user.getUserName()));

        var savedUser = userRepository.save(user);
        log.debug("User created successfully: {}", user);
        return userMapper.toCreateDTO(savedUser);
    }

    /**
     * Validates that the user is not null.
     *
     * @param user The user to be validated.
     * @throws HttpBadRequestException If the user is null.
     */
    private void validateUserNotNull(User user) {
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
    private void validateUserNameAndPasswordNotEmpty(User user) {
        if (StringUtils.isEmpty(user.getUserName()) || StringUtils.isEmpty(user.getPassword())) {
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
    private void validateUserNameNotTaken(String userName) {
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
    private void validateEmailNotRegistered(String email) {
        if (userRepository.existsByEmail(email)) {
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
     *         and symbol string.
     */
    private String generateNameTag(String username) {
        // Generate a random string of the specified length with alphanumeric and symbol characters
        var randomString = RandomStringUtils.random(6, true, true);

        return username + randomString;
    }


    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUserById(Long userId) {
        return null;
    }

    @Override
    public User getByUserName(String userName) {
        return null;
    }

    @Override
    public User getNameTag(String nameTag) {
        return null;
    }

    @Override
    public User authenticateUserByUserName(String userName, String password) {
        return null;
    }

    @Override
    public User authenticateUserByEmail(String email, String password) {
        return null;
    }

    @Override
    public Boolean changePassword(String userName, String oldPassword, String newPassword) {
        return true;
    }

    @Override
    public Boolean isUserNameAvailable(String userName) {
        return true;
    }

    @Override
    public Boolean isEmailAvailable(String email) {
        return true;
    }

    @Override
    public void deleteById(Long userId) {

    }

    @Override
    public List<Roles> getRolesByUserId(Long userId) {
        return null;
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.debug("Request to get all Users");
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

}
