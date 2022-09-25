package com.example.spring_project.dto;

import com.example.spring_project.domain.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {

    private Long userId;
    private String accountNumber;
    private Long balance;

    private LocalDateTime registerAt;
    private LocalDateTime unRegisterAt;

    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .registerAt(account.getRegisterAt())
                .unRegisterAt(account.getUnRegisterAt())
                .build();
    }
}
