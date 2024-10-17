package com.example.omega.service;


import com.example.omega.domain.Budget;
import com.example.omega.domain.User;
import com.example.omega.mapper.BudgetMapper;
import com.example.omega.repository.BudgetRepository;
import com.example.omega.service.dto.BudgetDTO;
import com.example.omega.service.exception.BadRequestException;
import com.example.omega.service.util.TransactionServiceUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    private final UserService userService;
    private final TransactionServiceUtil transactionServiceUtil;

    /**
     * Creates a new budget.
     *
     * @param budgetDTO the budget data transfer object containing the details of the budget to be created
     * @return the created budget as a BudgetDTO
     */
    public BudgetDTO createBudget(BudgetDTO budgetDTO) {
        log.debug("Creating new budget: {}", budgetDTO);
        var budget = budgetMapper.toEntity(budgetDTO);
        budget = budgetRepository.save(budget);
        userService.setUserBudgeting(budgetDTO.getUserId(), true);
        return budgetMapper.toDto(budget);
    }

    /**
     * Partially updates an existing budget.
     *
     * @param budgetDTO the budget data transfer object containing the details to be updated
     * @return an Optional containing the updated BudgetDTO if the budget was found, or an empty Optional if not
     */
    @Transactional
    public Optional<BudgetDTO> partialUpdate(BudgetDTO budgetDTO) {
        log.debug("Partially updating budget: {}", budgetDTO);

        return budgetRepository.findById(budgetDTO.getId())
                .map(existingBudget -> {
                    budgetMapper.partialUpdate(existingBudget, budgetDTO);
                    return existingBudget;
                })
                .map(budgetRepository::save)
                .map(budgetMapper::toDto);
    }

    /**
     * Retrieves a budget by the user ID.
     *
     * @param userId the ID of the user whose budget is to be retrieved
     * @return the BudgetDTO representing the retrieved budget
     * @throws BadRequestException if the budget is not found for the given user ID
     */
    public BudgetDTO getBudgetByUserId(Long userId) {
        log.debug("Fetching budget for user with id: {}", userId);
        var budget = budgetRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Budget not found for the given user ID"));
        return budgetMapper.toDto(budget);
    }

    /**
     * Deletes a budget by its ID.
     *
     * @param id the ID of the budget to be deleted
     */
    public void deleteBudget(Long id) {
        log.debug("Deleting budget with id: {}", id);
        userService.setUserBudgeting(getUserByBudgetId(id).getId(), false);
        budgetRepository.deleteById(id);
    }

    /**
     * Retrieves the most recent active budget for a specified user.
     * This method queries the budget repository to find the latest budget based on the end date for the given user ID.
     * It ensures that users always have access to their current budgeting information.
     *
     * @param userId The ID of the user whose current budget is being requested.
     * @return The most recent {@link Budget} entity for the specified user.
     * @throws BadRequestException if no active budget is found for the given user ID.
     */
    public Budget getCurrentBudget(Long userId) {
        return budgetRepository
                .findTopByUserIdOrderByEndDateDesc(userId)
                .orElseThrow(() -> new BadRequestException("No active budget not found"));
    }

    /**
     * Retrieves the User associated with a given budget ID.
     *
     * @param budgetId the ID of the budget
     * @return the User who owns the budget
     * @throws BadRequestException if the budget is not found
     */
    public User getUserByBudgetId(Long budgetId) {
        log.debug("Fetching User for budget with id: {}", budgetId);
        return budgetRepository.findById(budgetId)
                .map(Budget::getUser)
                .orElseThrow(() -> new BadRequestException("Budget not found"));
    }

    /**
     * Calculates the remaining budget amount for a specific user.
     * It subtracts the total amount spent by the user within the current budget's time range
     * from the user's current budget.
     *
     * @param userId The ID of the user for whom to calculate the remaining budget.
     * @return The remaining budget amount as a {@link BigDecimal}.
     */
    public BigDecimal calculateRemainingBudgetAmount(Long userId) {
        log.debug("Calculating remaining budget amount for user with id: {}", userId);

        var currentBudget = getCurrentBudget(userId);
        var totalSpent = transactionServiceUtil.calculateTotalSpentInTimeRange(userId, currentBudget);

        return currentBudget.getBudget().subtract(totalSpent);
    }

}