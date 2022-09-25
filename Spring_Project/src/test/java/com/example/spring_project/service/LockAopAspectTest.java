package com.example.spring_project.service;

import com.example.spring_project.dto.UseBalance;
import com.example.spring_project.exception.AccountException;
import com.example.spring_project.type.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {


    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @Test
    void lockAndUnlock() throws Throwable {

        ArgumentCaptor<String> lockArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unLockArgumentCaptor = ArgumentCaptor.forClass(String.class);

        UseBalance.Request request = new UseBalance.Request(123L,"1234",1000L);

        lockAopAspect.aroundMethod(proceedingJoinPoint, request);

        verify(lockService,times(1)).lock(lockArgumentCaptor.capture());
        verify(lockService,times(1)).unlock(unLockArgumentCaptor.capture());
        assertEquals("1234",lockArgumentCaptor.getValue());
        assertEquals("1234",unLockArgumentCaptor.getValue());
    }

    @Test
    void lockAndUnlock_evenIfThrow() throws Throwable {

        ArgumentCaptor<String> lockArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unLockArgumentCaptor = ArgumentCaptor.forClass(String.class);

        UseBalance.Request request = new UseBalance.Request(123L,"54321",1000L);
        given(proceedingJoinPoint.proceed())
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        assertThrows(AccountException.class , () ->
        lockAopAspect.aroundMethod(proceedingJoinPoint, request));

        verify(lockService,times(1)).lock(lockArgumentCaptor.capture());
        verify(lockService,times(1)).unlock(unLockArgumentCaptor.capture());
        assertEquals("54321",lockArgumentCaptor.getValue());
        assertEquals("54321",unLockArgumentCaptor.getValue());
    }

}