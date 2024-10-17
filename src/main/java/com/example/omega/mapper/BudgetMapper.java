package com.example.omega.mapper;

import com.example.omega.domain.Budget;
import com.example.omega.service.dto.BudgetDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "user.id", source = "userId")
    Budget toEntity(BudgetDTO budgetDTO);

    @Mapping(target = "userId", source = "user.id")
    BudgetDTO toDto(Budget budget);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Budget entity, BudgetDTO dto);
}