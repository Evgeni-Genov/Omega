package com.example.omega.web;

import com.example.omega.domain.Transaction;
import com.example.omega.service.TransactionService;
import com.example.omega.service.dto.CreditCardDTO;
import com.example.omega.service.dto.TransactionDTO;
import com.example.omega.service.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionResource {

    private final TransactionService transactionService;

    private final Javers javers;

    @PostMapping("/send-funds")
    @Operation(summary = "Send funds")
    public ResponseEntity<TransactionDTO> sendMoney(@RequestBody TransactionDTO transactionDTO) {
        log.debug("User is trying to send funds!");
        var createdTransactionDTO = transactionService.sendMoney(transactionDTO);
        return ResponseEntity.ok().body(createdTransactionDTO);
    }

    @GetMapping("/stores/snapshots")
    public String getStoresSnapshots() {
        QueryBuilder jqlQuery = QueryBuilder.byClass(Transaction.class);
        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());
        return javers.getJsonConverter().toJson(snapshots);
    }

    @GetMapping("/all-transactions/{userId}")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionsForUser(Pageable pageable, @PathVariable Long userId) {
        log.debug("REST request to get a page of Transactions");
        var transactionsPage = transactionService.getAllTransactionsForAUser(pageable, userId);
        var headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), transactionsPage);
        return ResponseEntity.ok().headers(headers).body(transactionsPage.getContent());
    }

    //TODO:
    @PostMapping("/add-funds")
    public ResponseEntity<TransactionDTO> addFunds(@Valid @RequestBody CreditCardDTO creditCardDTO) {
        log.debug("User is trying to add funds!");
        var createdCreditCardDTO = transactionService.addFunds(creditCardDTO);
        return ResponseEntity.ok().body(createdCreditCardDTO);
    }
}
