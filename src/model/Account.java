package model;
import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;

import java.util.Objects;

public abstract class Account implements Transactable, InterestBearing {
    @Id
    private final String accountNumber;
    @MaxLength(40)
    private final String ownerName;
    @Positive
    private long balance;
    private boolean active;

    private static int accountCounter = 0;

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", ++accountCounter);
        this.ownerName = ownerName;
        this.balance = 0;
        this.active = true;

        if (openingBalance > 0) {
            if (openingBalance < 0) {
                throw new IllegalArgumentException("Opening balance cannot be negative.");
            }
            this.balance = openingBalance;
        }
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be positive.");
        }

        balance += amount;
    }

    public boolean withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be positive.");
        }

        if (!canWithdraw(amount)) {
            long shortfall = amount - balance;

            throw new InsufficientFundsException(
                    "Insufficient funds for withdrawal.", shortfall);
        }

        balance -= amount;
        return true;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "model.Account Number: " + accountNumber
                + ", Owner: " + ownerName
                + ", Balance: ₹" + balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Account)) {
            return false;
        }

        Account other = (Account) o;
        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
    public abstract double interestRate();
    public abstract boolean canWithdraw(long amount);
    public void transfer(Account to, long amount)
            throws exception.BankException {
        try {
            this.withdraw(amount);
            to.deposit(amount);
        } catch (exception.BankException e) {
            throw e;
        } finally {
            System.out.println("Transfer attempt completed.");
        }
    }
}
