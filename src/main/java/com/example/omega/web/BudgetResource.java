package com.example.omega.web;

import com.example.omega.service.BudgetService;
import com.example.omega.service.TransactionService;
import com.example.omega.service.dto.BudgetDTO;
import com.example.omega.service.util.SecurityUtils;
import com.example.omega.service.util.TransactionServiceUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class BudgetResource {

    //TODO: principal is null?

    private final BudgetService budgetService;
    private final SecurityUtils securityUtils;
    private final TransactionService transactionService;
    private final TransactionServiceUtil transactionServiceUtil;

    @PostMapping("/budgets")
    public ResponseEntity<BudgetDTO> createBudget(Principal principal,
                                                  @RequestBody BudgetDTO budgetDTO) {
        log.debug("REST request to save Budget: {}", budgetDTO);
        var result = budgetService.createBudget(budgetDTO);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/budgets/{id}")
    public ResponseEntity<BudgetDTO> partialUpdateBudget(Principal principal,
                                                         @PathVariable Long id,
                                                         @RequestBody BudgetDTO budgetDTO) {
        log.debug("REST request to partially update Budget: {}", budgetDTO);
        budgetDTO.setId(id);
        var result = budgetService.partialUpdate(budgetDTO);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //TODO: maybe refactor this to call get Budget by user id instead of long id
    @GetMapping("/budgets/{userId}")
    public ResponseEntity<BudgetDTO> getBudgetByUserId(Principal principal,
                                                       @PathVariable Long userId) {
        log.debug("REST request to get Budget for User with ID: {}", userId);
        var budgetDTO = budgetService.getBudgetByUserId(userId);
        return ResponseEntity.ok(budgetDTO);
    }

    @DeleteMapping("/budgets/{id}")
    public ResponseEntity<Void> deleteBudget(Principal principal,
                                             @PathVariable Long id) {
        log.debug("REST request to delete Budget: {}", id);
//        var user = budgetService.getUserByBudgetId(id);
//        securityUtils.canCurrentUserAccessThisData(principal, user.getId());
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/budgets-remaining/{userId}")
    public ResponseEntity<BigDecimal> calculateRemainingBudgetAmount(Principal principal,
                                                                     @PathVariable Long userId) {
        log.debug("REST request to get remaining budget amount for User with ID: {}", userId);
        return ResponseEntity.ok(budgetService.calculateRemainingBudgetAmount(userId));
    }

    @GetMapping("/budgets/{userId}/total-spent")
    public ResponseEntity<BigDecimal> getTotalSpentInBudget(Principal principal,
                                                            @PathVariable Long userId) {
        log.debug("REST request to get total spent amount for User with ID: {}", userId);
        var budget = budgetService.getCurrentBudget(userId);
        var totalSpent = transactionServiceUtil.calculateTotalSpentInTimeRange(userId, budget);
        return ResponseEntity.ok(totalSpent);
    }

}
