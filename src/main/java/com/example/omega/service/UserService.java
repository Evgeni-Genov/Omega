package com.example.omega.service;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.service.dto.UserCreateDTO;
import com.example.omega.service.dto.UserCredentialUpdateDTO;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UserService {

    UserCreateDTO createUser(UserCreateDTO userCreateDTO);

    UserUpdateDTO updateUser(User user);

    UserCredentialUpdateDTO updateUserCredentials(User user);

    UserDTO getUserById(Long userId);

    User getUserByUserName(String userName);

    User getUserByNameTag(String nameTag);

    User authenticateUserByUserName(String userName, String password);

    User authenticateUserByEmail(String email, String password);

    Boolean changePassword(String userName, String oldPassword, String newPassword);

    Boolean isUserNameAvailable(String userName);

    Boolean isEmailAvailable(String email);

    void deleteById(Long userId);

    List<Roles> getRolesByUserId(Long userId);

    Page<UserDTO> getAllUsers(Pageable pageable);
}
