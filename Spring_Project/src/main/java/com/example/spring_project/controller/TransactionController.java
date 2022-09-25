package com.example.spring_project.controller;

import com.example.spring_project.aop.AccountLock;
import com.example.spring_project.dto.CancelBalance;
import com.example.spring_project.dto.QueryTransactionResponse;
import com.example.spring_project.dto.TransactionDto;
import com.example.spring_project.dto.UseBalance;
import com.example.spring_project.exception.AccountException;
import com.example.spring_project.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @AccountLock
    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(@Valid @RequestBody UseBalance.Request request) throws InterruptedException {

        try {
            Thread.sleep(3000L);
            return UseBalance.Response.from(transactionService.useBalance(request.getUserId(),request.getAccountNumber(),request.getAmount()));
        }catch (AccountException e) {
            log.error("Failed to use balance.");

            transactionService.saveFailedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }


    }

    @AccountLock
    @PostMapping("/transaction/cancel")
    public CancelBalance.Response cancelBalance(@Valid @RequestBody CancelBalance.Request request) {

        try {
            return CancelBalance.Response.from(transactionService.cancelBalance(request.getTransactionId(),request.getAccountNumber(),request.getAmount()));
        }catch (AccountException e) {
            log.error("Failed to use balance.");

            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }


    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(@PathVariable String transactionId) {

        return QueryTransactionResponse.from(transactionService.queryTransaction(transactionId));
    }

}
