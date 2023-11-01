package com.example.omega.mapper;

import com.example.omega.domain.Transaction;
import com.example.omega.domain.User;
import com.example.omega.service.dto.TransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "sender", target = "senderId")
    @Mapping(source = "recipient", target = "recipientId")
    TransactionDTO toDTO(Transaction transaction);

    @Mapping(source = "senderId", target = "sender.id")
    @Mapping(source = "recipientId", target = "recipient.id")
    Transaction toEntity(TransactionDTO transactionDTO);

    default User fromId(Long userId) {
        if (userId == null) {
            return null;
        }
        var user = new User();
        user.setId(userId);
        return user;
    }

    default Long fromUser(User user) {
        if (user == null) {
            return null;
        }
        return user.getId();
    }

}
