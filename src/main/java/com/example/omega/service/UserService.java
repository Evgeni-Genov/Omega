package com.example.omega.service;

import com.example.omega.domain.User;
import com.example.omega.domain.enumeration.Roles;
import com.example.omega.service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UserService {

    UserCreateDTO createUser(UserCreateDTO userCreateDTO);

    UserUpdateDTO updateUserNonCredentialInformation(Long id , UserUpdateDTO userUpdateDTO);

    UserCredentialUpdateDTO updateUserCredentials(User user);

    UserDTO getUserById(Long userId);

    UserSearchDTO getUserByNameTag(String nameTag);

    User authenticateUserByEmail(String email, String password);

    UserCredentialUpdateDTO changePassword(Long userId, String oldPassword, String newPassword);

    UserCredentialUpdateDTO changeEmail(Long userId, String newEmail);

    Boolean isUserNameAvailable(String userName);

    Boolean isEmailAvailable(String email);

    void deleteById(Long userId);

    UserCredentialUpdateDTO enableUserTwoStepVerification(Long userId);

    UserCredentialUpdateDTO disableUserTwoStepVerification(Long userId);

    List<Roles> getRolesByUserId(Long userId);

    Page<UserDTO> getAllUsers(Pageable pageable);
}
