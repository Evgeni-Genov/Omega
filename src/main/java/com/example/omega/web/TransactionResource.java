package com.example.omega.web;

import com.example.omega.domain.Transaction;
import com.example.omega.service.TransactionService;
import com.example.omega.service.dto.TransactionDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transaction")
@Slf4j
@AllArgsConstructor
public class TransactionResource {

    private final TransactionService transactionService;

    private final Javers javers;

    @PostMapping("/send-funds")
    @Operation(summary = "Send funds")
    public ResponseEntity<TransactionDTO> sendMoney(Principal principal, @RequestBody TransactionDTO transactionDTO) {
        log.debug("User: {} is trying to send funds!", principal.getName());
        var createdTransactionDTO = transactionService.sendMoney(transactionDTO);
        return ResponseEntity.ok().body(createdTransactionDTO);
    }

    @GetMapping("/stores/snapshots")
    public String getStoresSnapshots() {
        QueryBuilder jqlQuery = QueryBuilder.byClass(Transaction.class);
        List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());
        return javers.getJsonConverter().toJson(snapshots);
    }
}
