package com.example.omega.web;

import com.example.omega.service.TransactionReportService;
import com.example.omega.service.util.SecurityUtils;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class TransactionReportResource {

    private final TransactionReportService transactionReportService;
    private final SecurityUtils securityUtils;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/transactions-report")
    public ResponseEntity<InputStreamResource> downloadTransactionReport(
            Principal principal,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) throws DocumentException {

        var userId = securityUtils.extractCurrentUserIdFromPrincipal(principal);
        var pdfBytes = transactionReportService.generateTransactionReport(userId, startDate, endDate);
        var inputStream = new ByteArrayInputStream(pdfBytes);

        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=transaction_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
}
