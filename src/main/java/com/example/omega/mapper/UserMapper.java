package com.example.omega.mapper;

import com.example.omega.config.security.payload.request.SignupRequest;
import com.example.omega.domain.User;
import com.example.omega.service.dto.*;
import org.mapstruct.Mapper;

//TODO: possible errors converting from one object to another(fields that are not updated will be null)
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);

    User toUserAuth(SignupRequest request);

}
