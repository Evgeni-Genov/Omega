package com.example.omega.mapper;

import com.example.omega.domain.User;
import com.example.omega.service.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    User toUser(UserDTO userDTO);
}
