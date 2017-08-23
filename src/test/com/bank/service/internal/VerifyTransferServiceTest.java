package com.bank.service.internal;

import com.bank.domain.Account;
import com.bank.domain.InsufficientFundsException;
import com.bank.repository.internal.JdbcAccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class VerifyTransferServiceTest {

    @Mock
    private JdbcAccountRepository accountRepo;

    @InjectMocks
    private VerifyTransferService transferService;

    private Account accountA;
    private Account accountB;

    @Before
    public void setUp() {
        accountA = new Account("1", "1234567890", "John Smith1", 500.00);
        accountB = new Account("2", "1234567800", "John Smith2", 1000.00);

        doReturn(accountA).when(accountRepo).findByAccountNumber("1234567890");
        doReturn(accountB).when(accountRepo).findByAccountNumber("1234567800");
    }

    @Test
    public void testTransferService() throws Exception, InvalidTransferWindow {
        double amountABefore = accountA.getBalance();
        double amountBBefore = accountB.getBalance();
        double amount = 20.00;

        transferService.transfer(amount, "1234567890", "1234567800", "Transfer 20B from A to B.");

        assertTrue(amount == amountABefore - accountA.getBalance());
        assertTrue(amount == accountB.getBalance() - amountBBefore);
    }

    @Test(expected = InvalidTransferWindow.class)
    public void itShouldThrowInvalidTransferWindowIfAccountSrcNotExist() throws InsufficientFundsException, InvalidTransferWindow {
        transferService.transfer(20.00, "1234567899", "1234567800", "Transfer 20B from A to B.");

    }

    @Test(expected = InvalidTransferWindow.class)
    public void itShouldThrowInvalidTransferWindowIfAccountDestNotExist() throws InsufficientFundsException, InvalidTransferWindow {
        transferService.transfer(20.00, "1234567890", "1234567811", "Transfer 20B from A to B.");

    }

    @Test(expected = InsufficientFundsException.class)
    public void itShouldThrowInsufficientFundsExceptionIfBalanceNotEnough() throws InsufficientFundsException, InvalidTransferWindow {
        transferService.transfer(1000, "1234567890", "1234567800", "Transfer 1000B from A to B.");

    }

}