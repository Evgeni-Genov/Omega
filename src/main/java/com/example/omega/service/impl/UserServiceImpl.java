package com.example.omega.service.impl;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        return null;
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
    public List<User> getAllUsers() {
        return null;
    }
}
