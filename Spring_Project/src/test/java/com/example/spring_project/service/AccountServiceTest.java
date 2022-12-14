package com.example.spring_project.service;

import com.example.spring_project.domain.Account;
import com.example.spring_project.domain.AccountUser;
import com.example.spring_project.dto.AccountDto;
import com.example.spring_project.exception.AccountException;
import com.example.spring_project.repository.AccountUserRepository;
import com.example.spring_project.type.AccountStatus;
import com.example.spring_project.repository.AccountRepository;
import com.example.spring_project.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountUserRepository accountUserRepository;


    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccountSuccess() {

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                                .accountUser(user)
                                .accountNumber("1000000012").build()));
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService.createAccount(1L,1000L);

        verify(accountRepository,times(1)).save(captor.capture());
        assertEquals(12L,accountDto.getUserId());
        assertEquals("1000000013",captor.getValue().getAccountNumber());
    }

    @Test
    void deleteAccountSuccess() {

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                                .balance(0L)
                        .accountNumber("1000000012").build()));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService.deleteAccount(1L,"1234567890");

        verify(accountRepository,times(1)).save(captor.capture());
        assertEquals(12L,accountDto.getUserId());
        assertEquals("1000000012",captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED,captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
    void deleteAccount_UserNotFound() {

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(15L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        AccountException accountException = assertThrows(AccountException.class,() ->
                accountService.deleteAccount(1L,"1234567890"));


        assertEquals(ErrorCode.USER_NOT_FOUND,accountException.getErrorCode());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
    void deleteAccount_AccountNotFound() {

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(15L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());


        AccountException accountException = assertThrows(AccountException.class,() ->
                accountService.deleteAccount(1L,"1234567890"));


        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND,accountException.getErrorCode());
    }

    @Test
    @DisplayName("?????? ????????? ??????")
    void deleteAccountFailed_userUnMath() {

        AccountUser Pobi = AccountUser.builder()
                .name("Pobi").build();
        Pobi.setId(12L);
        AccountUser Harry = AccountUser.builder()
                .name("Harry").build();
        Harry.setId(13L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Harry)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        AccountException exception = assertThrows(AccountException.class,() ->
            accountService.deleteAccount(1L,"1234567890"));

        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH,exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ????????? ????????? ????????? ??????.")
    void deleteAccountFailed_balanceNotEmpty() {

        AccountUser Pobi = AccountUser.builder()
                .name("Pobi").build();
        Pobi.setId(12L);
        AccountUser Harry = AccountUser.builder()
                .name("Harry").build();
        Harry.setId(13L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Pobi)
                        .balance(100L)
                        .accountNumber("1000000012").build()));

        AccountException exception = assertThrows(AccountException.class,() ->
                accountService.deleteAccount(1L,"1234567890"));

        assertEquals(ErrorCode.BALANCE_NOT_EMPTY,exception.getErrorCode());
    }

    @Test
    @DisplayName("?????? ????????? ????????? ??? ??????.")
    void deleteAccountFailed_alreadyUnRegistered() {

        AccountUser Pobi = AccountUser.builder()
                .name("Pobi").build();
        Pobi.setId(12L);
        AccountUser Harry = AccountUser.builder()
                .name("Harry").build();
        Harry.setId(13L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Pobi)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        AccountException exception = assertThrows(AccountException.class,() ->
                accountService.deleteAccount(1L,"1234567890"));

        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED,exception.getErrorCode());
    }



    @Test
    void createFirstAccount() {

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(15L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        AccountDto accountDto = accountService.createAccount(1L,1000L);

        verify(accountRepository,times(1)).save(captor.capture());
        assertEquals(15L,accountDto.getUserId());
        assertEquals("1000000000",captor.getValue().getAccountNumber());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ??????")
    void createAccount_UserNotFound() {

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(15L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        AccountException accountException = assertThrows(AccountException.class,() ->
                accountService.createAccount(1L,1000L));


        assertEquals(ErrorCode.USER_NOT_FOUND,accountException.getErrorCode());
    }

    @Test
    @DisplayName("?????? ??? ?????? ????????? 10???")
    void createAccount_maxAccountIs10() {

        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(15L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.countByAccountUser(any()))
                .willReturn(10);

        AccountException accountException = assertThrows(AccountException.class,() ->
                accountService.createAccount(1L,1000L));


        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10,accountException.getErrorCode());
    }

    @Test
    void successGetAccountByUserId() {

        AccountUser Pobi = AccountUser.builder()
                .name("Pobi").build();
        Pobi.setId(15L);
        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountUser(Pobi)
                .accountNumber("1111111111")
                .balance(1000L)
                .build(),
                Account.builder()
                        .accountUser(Pobi)
                        .accountNumber("2222222222")
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .accountUser(Pobi)
                        .accountNumber("3333333333")
                        .balance(3000L)
                        .build());
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));
        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);

        List<AccountDto> accountDtos = accountService.getAccountByUserId(1L);

        assertEquals(3,accountDtos.size());
        assertEquals("1111111111",accountDtos.get(0).getAccountNumber());
        assertEquals(1000,accountDtos.get(0).getBalance());
        assertEquals("2222222222",accountDtos.get(1).getAccountNumber());
        assertEquals(2000,accountDtos.get(1).getBalance());
        assertEquals("3333333333",accountDtos.get(2).getAccountNumber());
        assertEquals(3000,accountDtos.get(2).getBalance());

    }

    @Test
    void failedToGetAccounts() {

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        AccountException accountException = assertThrows(AccountException.class,() ->
                accountService.getAccountByUserId(1000L));


        assertEquals(ErrorCode.USER_NOT_FOUND,accountException.getErrorCode());
    }



}