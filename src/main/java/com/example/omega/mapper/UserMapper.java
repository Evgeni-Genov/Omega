package com.example.omega.mapper;

import com.example.omega.domain.User;
import com.example.omega.service.dto.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    UserCreateDTO toCreateDTO(User user);

    UserUpdateDTO toUpdateDTO(User user);

    UserCredentialUpdateDTO toUserCredentialUpdateDTO(User user);

    UserSearchDTO toUserSearchDTO(User user);

    User toEntity(UserDTO userDTO);

    User toEntity(UserCreateDTO userCreateDTO);

    User toEntity(UserUpdateDTO userUpdateDTO);

    User toEntity(UserCredentialUpdateDTO userCredentialUpdateDTO);

    User toEntity(UserSearchDTO userSearchDTO);


}
