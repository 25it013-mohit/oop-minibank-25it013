package service;

import model.Account;
import model.SavingsAccount;
import exception.BankException;

public class DeadlockDemo {

    static void transferSafely(Account from, Account to, long amount)
            throws BankException {

        Account first;
        Account second;

        if (from.getAccountNumber().compareTo(to.getAccountNumber()) < 0) {
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        synchronized (first) {
            synchronized (second) {
                from.transfer(to, amount);
                System.out.println(
                        Thread.currentThread().getName()
                                + " transferred " + amount
                                + " from " + from.getAccountNumber()
                                + " to " + to.getAccountNumber()
                );
            }
        }
    }

    public static void main(String[] args) {
        Account accountA = new SavingsAccount("Account A", 10000, 1000);
        Account accountB = new SavingsAccount("Account B", 10000, 1000);

        Thread thread1 = new Thread(() -> {
            try {
                transferSafely(accountA, accountB, 1000);
            } catch (BankException e) {
                System.out.println(e.getMessage());
            }
        }, "Thread-1");

        Thread thread2 = new Thread(() -> {
            try {
                transferSafely(accountB, accountA, 2000);
            } catch (BankException e) {
                System.out.println(e.getMessage());
            }
        }, "Thread-2");

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Account A balance: " + accountA.getBalance());
        System.out.println("Account B balance: " + accountB.getBalance());
        System.out.println("Both transfers completed.");
    }
}