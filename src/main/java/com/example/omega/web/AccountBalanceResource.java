package com.example.omega.web;

import com.example.omega.service.AccountBalanceService;
import com.example.omega.service.dto.AccountBalanceDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/account-balance")
@Slf4j
public class AccountBalanceResource {

    private final AccountBalanceService accountBalanceService;

    @PostMapping("/account-balances")
    public ResponseEntity<AccountBalanceDTO> createAccountBalance(@RequestBody AccountBalanceDTO accountBalanceDTO) {
        var result = accountBalanceService.create(accountBalanceDTO);
        return ResponseEntity.ok().body(result);
    }

    @PatchMapping("/account-balances")
    public ResponseEntity<AccountBalanceDTO> partialUpdateAccountBalance(@RequestBody AccountBalanceDTO accountBalanceDTO) {
        var result = accountBalanceService.partialUpdate(accountBalanceDTO);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/account-balances/user/{userId}")
    public ResponseEntity<List<AccountBalanceDTO>> getAllForUser(Pageable pageable, @PathVariable Long userId) {
        var page = accountBalanceService.getAllForUser(pageable, userId);
        return ResponseEntity.ok().body(page.getContent());
    }

    @GetMapping("/account-balances/{id}")
    public ResponseEntity<AccountBalanceDTO> getOne(@PathVariable Long id) {
        var accountBalanceDTO = accountBalanceService.getOne(id);
        return accountBalanceDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/account-balances/{id}")
    public ResponseEntity<Void> deleteAccountBalance(@PathVariable Long id) {
        accountBalanceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
