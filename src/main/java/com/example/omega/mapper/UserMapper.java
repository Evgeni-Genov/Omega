package com.example.omega.mapper;

import com.example.omega.config.security.payload.request.SignupRequest;
import com.example.omega.domain.User;
import com.example.omega.service.dto.FriendDTO;
import com.example.omega.service.dto.UserDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "isBudgetingEnabled", source = "user.isBudgetingEnabled")
    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);

    User toUserAuth(SignupRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "friendsList", ignore = true)
    void updateUserFromDto(UserDTO userDTO, @MappingTarget User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nameTag", source = "nameTag")
    FriendDTO toFriendDTO(User user);
}
