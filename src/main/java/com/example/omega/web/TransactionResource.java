package com.example.omega.web;

import com.example.omega.domain.Transaction;
import com.example.omega.service.TransactionService;
import com.example.omega.service.UserService;
import com.example.omega.service.dto.CreditCardDTO;
import com.example.omega.service.dto.TransactionDTO;
import com.example.omega.service.dto.TransactionSummaryDTO;
import com.example.omega.service.util.PaginationUtil;
import com.example.omega.service.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api")
public class TransactionResource {

    private final TransactionService transactionService;
    private final Javers javers;
    private final UserService userService;
    private SecurityUtils securityUtils;

    @PostMapping("/transaction/send-funds")
    @Operation(summary = "Send funds")
    public ResponseEntity<TransactionDTO> sendFunds(@RequestBody TransactionDTO transactionDTO) {
        log.debug("User is trying to send funds!");
        var createdTransactionDTO = transactionService.sendFunds(transactionDTO);
        return ResponseEntity.ok().body(createdTransactionDTO);
    }

    //TODO: basic Javers Snapshot
    @GetMapping("/stores/snapshots")
    public String getStoresSnapshots() {
        var jqlQuery = QueryBuilder.byClass(Transaction.class);
        var snapshots = javers.findSnapshots(jqlQuery.build());
        return javers.getJsonConverter().toJson(snapshots);
    }

    @GetMapping("/snapshots/{transactionId}")
    public String getTransactionSnapshots(@PathVariable Long transactionId) {
        var jqlQuery = QueryBuilder.byInstanceId(transactionId, Transaction.class);
        var snapshots = javers.findSnapshots(jqlQuery.build());
        return javers.getJsonConverter().toJson(snapshots);
    }

    @GetMapping("/transaction/{userId}")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsForUser(Pageable pageable, @PathVariable Long userId) {
        log.debug("REST request to get a page of Transactions");
        var transactionsPage = transactionService.getAllTransactionsForAUser(pageable, userId);
        var headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), transactionsPage);
        return ResponseEntity.ok().headers(headers).body(transactionsPage.getContent());
    }

    @PostMapping("/transaction/add-funds")
    public ResponseEntity<TransactionDTO> addFunds(@Valid @RequestBody CreditCardDTO creditCardDTO) {
        log.debug("User is trying to add funds!");
        var createdCreditCardDTO = transactionService.addFunds(creditCardDTO);
        return ResponseEntity.ok().body(createdCreditCardDTO);
    }

    //TODO: should include flag isFriend
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionSummaryDTO>> getAllTransactionsBetweenTwoUsers(Principal principal, Pageable pageable,
                                                                                         @RequestParam("userId") Long userId,
                                                                                         @RequestParam("otherUserNameTag") String otherUserNameTag) {
        var user = securityUtils.extractCurrentUserIdFromPrincipal(principal);
        log.debug("User with ID: {} is trying to get all transactions between two users!", user);
        var otherUserId = userService.getUserByNameTag(otherUserNameTag).getId();
        var transactionsPage = transactionService.getAllTransactionBetweenTwoUsers(pageable, userId, otherUserId);
        var headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), transactionsPage);
        return ResponseEntity.ok().headers(headers).body(transactionsPage.getContent());
    }

    @PostMapping("/transaction/request-funds")
    @Operation(summary = "Request funds")
    public ResponseEntity<TransactionDTO> requestFunds(@RequestBody TransactionDTO transactionDTO,
                                                       Principal principal) {
        var currentUserId = securityUtils.extractCurrentUserIdFromPrincipal(principal);
        log.debug("User with ID: {} is trying to request funds!", currentUserId);
        transactionDTO.setSenderId(currentUserId);
        var createdTransactionDTO = transactionService.requestFunds(transactionDTO);
        return ResponseEntity.ok().body(createdTransactionDTO);
    }
}
