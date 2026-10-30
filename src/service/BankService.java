package service;

import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;

import exception.BankException;
import exception.InvalidAmountException;

import util.StatementFormatter;

import java.util.ArrayList;
import java.util.List;

public class BankService {

    private final Repository<Account> repository = new service.Repository<Account>();

    public Account openAccount(
            String ownerName,
            String accountType,
            long openingBalance,
            long accountLimit) {

        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner name cannot be empty.");
        }

        if (openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative.");
        }

        Account account;

        switch (accountType.trim().toUpperCase()) {
            case "SAVINGS" ->
                    account = new SavingsAccount(
                            ownerName, openingBalance, accountLimit);

            case "CURRENT" ->
                    account = new CurrentAccount(
                            ownerName, openingBalance, accountLimit);

            case "FIXED_DEPOSIT" ->
                    account = new FixedDepositAccount(
                            ownerName, openingBalance);

            default ->
                    throw new IllegalArgumentException(
                            "Invalid account type.");
        }

        repository.save(account);
        return account;
    }

    public void registerAccount(Account account) {
        repository.save(account);
    }

    public Account getAccount(String accountNumber) {
        return repository.findById(accountNumber);
    }

    public void deposit(String accountNumber, long amount)
            throws BankException {

        Account account = requireAccount(accountNumber);

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be positive.");
        }

        account.deposit(amount);
    }

    public void withdraw(String accountNumber, long amount)
            throws BankException {

        Account account = requireAccount(accountNumber);

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be positive.");
        }

        account.withdraw(amount);
    }

    public void transfer(
            String sourceNumber,
            String destinationNumber,
            long amount) throws BankException {

        Account source = requireAccount(sourceNumber);
        Account destination = requireAccount(destinationNumber);

        if (source == destination) {
            throw new IllegalArgumentException(
                    "Source and destination accounts must be different.");
        }

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be positive.");
        }

        source.transfer(destination, amount);
    }

    public String statement(String accountNumber) {
        return StatementFormatter.buildStatement(
                requireAccount(accountNumber));
    }

    public List<Account> listAccounts() {
        return new ArrayList<>(repository.findAll());
    }

    private Account requireAccount(String accountNumber) {
        Account account = repository.findById(accountNumber);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Account not found: " + accountNumber);
        }

        return account;
    }
}