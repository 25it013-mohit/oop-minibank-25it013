package service;

import model.Account;
import model.SavingsAccount;

import java.nio.file.Path;

public class StatePersisterDemo {

    public static void main(String[] args) throws Exception {
        Account[] accounts = {
            new SavingsAccount("Rahul", 10000, 1000),
            new SavingsAccount("Priya", 15000, 2000)
        };

        Path file = Path.of("data", "accounts.ser");

        java.nio.file.Files.createDirectories(file.getParent());

        StatePersister.save(accounts, file);
        System.out.println("Accounts saved successfully.");

        Account[] loadedAccounts = StatePersister.load(file);

        for (Account account : loadedAccounts) {
            System.out.println(
                account.getAccountNumber() + " | "
                + account.getOwnerName() + " | "
                + account.getBalance()
            );
        }
    }
}
