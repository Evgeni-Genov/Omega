package com.example.omega.mapper;

import com.example.omega.domain.User;
import com.example.omega.service.dto.UserCreateDTO;
import com.example.omega.service.dto.UserCredentialUpdateDTO;
import com.example.omega.service.dto.UserDTO;
import com.example.omega.service.dto.UserUpdateDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    UserCreateDTO toCreateDTO(User user);

    UserUpdateDTO toUpdateDTO(User user);

    UserCredentialUpdateDTO toUserCredentialUpdateDTO(User user);

    User toEntity(UserDTO userDTO);

    User toEntity(UserCreateDTO userCreateDTO);

    User toEntity(UserCredentialUpdateDTO userCredentialUpdateDTO);


}
