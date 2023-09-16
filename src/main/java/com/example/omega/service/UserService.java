package com.example.omega.service;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;

import java.util.List;


public interface UserService {

    User createUser(User user);

    User updateUser(User user);

    User getUserById(Long userId);

    User getByUserName(String userName);

    User getNameTag(String nameTag);

    User authenticateUserByUserName(String userName, String password);

    User authenticateUserByEmail(String email, String password);

    Boolean changePassword(String userName, String oldPassword, String newPassword);

    Boolean isUserNameAvailable(String userName);

    Boolean isEmailAvailable(String email);

    void deleteById(Long userId);

    List<Roles> getRolesByUserId(Long userId);

    List<User> getAllUsers();
}
