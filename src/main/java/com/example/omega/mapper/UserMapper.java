package com.example.omega.mapper;

import com.example.omega.config.security.payload.request.SignupRequest;
import com.example.omega.domain.User;
import com.example.omega.service.dto.*;
import org.mapstruct.Mapper;

//TODO: possible errors converting from one object to another(fields that are not updated will be null)
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    UserCreateDTO toCreateDTO(User user);

    UserUpdateDTO toUpdateDTO(User user);

    UserSecurityUpdateDTO toUserCredentialUpdateDTO(User user);

    UserSearchDTO toUserSearchDTO(User user);

    UserPasswordChangeDTO toUserPasswordChangeDTO(User user);

    User toEntity(UserDTO userDTO);

    User toEntity(UserCreateDTO userCreateDTO);

    User toEntity(UserUpdateDTO userUpdateDTO);

    User toEntity(UserSecurityUpdateDTO userSecurityUpdateDTO);

    User toEntity(UserSearchDTO userSearchDTO);

    User toEntity(UserPasswordChangeDTO userPasswordChangeDTO);

    User toUserAuth(SignupRequest request);

}
