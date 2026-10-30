package service;

import model.Account;
import exception.BankException;

public class TransactionTask implements Runnable {

    private final Account account;
    private final boolean isDeposit;
    private final long amount;

    public TransactionTask(Account account, boolean isDeposit, long amount) {
        this.account = account;
        this.isDeposit = isDeposit;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            if (isDeposit) {
                account.deposit(amount);
                System.out.println(
                        Thread.currentThread().getName()
                                + " deposited " + amount
                );
            } else {
                account.withdraw(amount);
                System.out.println(
                        Thread.currentThread().getName()
                                + " withdrew " + amount
                );
            }
        } catch (BankException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }
}