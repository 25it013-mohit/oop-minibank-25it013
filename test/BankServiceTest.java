import exception.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import exception.BankException;
import model.Account;
import service.BankService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankServiceTest {

    @Test
    void depositIncreasesBalance() throws exception.BankException {
        BankService bankService = new BankService();

        Account account = bankService.openAccount(
                "Test User",
                "SAVINGS",
                1000,
                500
        );

        bankService.deposit(account.getAccountNumber(), 500);

        assertEquals(1500, account.getBalance());
    }
    @Test
    void withdrawalOverLimitThrowsException() {
        BankService bankService = new BankService();

        Account account = bankService.openAccount(
                "Test User", "SAVINGS", 1000, 500
        );

        Assertions.assertThrows(
                BankException.class,
                () -> bankService.withdraw(
                        account.getAccountNumber(), 2000
                )
        );
    }
    @Test
    void concurrentDepositsKeepBalanceCorrect() throws InterruptedException {
        BankService bankService = new BankService();

        Account account = bankService.openAccount(
                "Test User", "SAVINGS", 1000, 500
        );

        int numberOfDeposits = 100;

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < numberOfDeposits; i++) {
            executor.submit(() ->
                    {
                        try {
                            account.deposit(10);
                        } catch (InvalidAmountException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }

        executor.shutdown();
        boolean completed = executor.awaitTermination(10, TimeUnit.SECONDS);

        Assertions.assertTrue(completed, "Deposits did not finish in time");
        assertEquals(2000L, account.getBalance());
    }
}