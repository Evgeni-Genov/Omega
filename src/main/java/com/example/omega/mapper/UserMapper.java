package com.example.omega.mapper;

import com.example.omega.domain.User;
import com.example.omega.service.dto.UserCreateDTO;
import com.example.omega.service.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserCreateDTO toCreateDTO(User user);

    UserDTO toDTO(User user);

    User toEntity(UserCreateDTO userCreateDTO);

    User toEntity(UserDTO userDTO);


}
