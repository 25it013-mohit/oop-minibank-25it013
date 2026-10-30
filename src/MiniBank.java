import model.Account;
import model.*;
import service.*;
import util.*;
import static util.Validator.isValidEmail;
import java.util.Scanner;
import util.AnnotationValidator;
import java.util.List;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.util.ArrayList;

public class MiniBank {
    record BankInfo(String name, String branch){}
    enum MenuOption{
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }
    static class DemoResource implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Resource closed.");
        }
    }

    static void main(String[] args) {
        testTransactionProcessor();
        demonstrateConcurrency();
        try (DemoResource resource = new DemoResource()) {
            System.out.println("Using resource.");
        }
        model.Account testAccount1 =
                new model.SavingsAccount("Test", 500, 100);

        try {
            testAccount1.withdraw(1000);
        } catch (exception.InsufficientFundsException e) {
            System.out.println(e.getMessage());
            System.out.println("Shortfall: " + e.getShortfall());
        } catch (exception.InvalidAmountException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Withdrawal attempt finished.");
        }
        Account negativeAccount = new SavingsAccount("Test", 500, 100);

        List<String> errors = AnnotationValidator.validate(negativeAccount);

        errors.forEach(System.out::println);
        BankInfo bank = new BankInfo(
                "MiniBank",
                "CHARUSAT"
        );

        Scanner sc = new Scanner(System.in);
        List<Account> accounts = new ArrayList<>();
        System.out.println("================================");
        System.out.println(bank.name());
        System.out.println("Branch: "+bank.branch());
        System.out.println("================================");
        boolean running = true;

        Account testAccount = new SavingsAccount("Riya", 5000, 1000);

        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.getBalance() - amount >= 1000;
            }
        };

        WithdrawRule lambdaRule =
                (account, amount) -> account.getBalance() - amount >= 1000;

        System.out.println("Anonymous class: "
                + anonymousRule.allow(testAccount, 2000));

        System.out.println("Lambda: "
                + lambdaRule.allow(testAccount, 2000));
        System.out.println("Yearly interest: ₹"
                + testAccount.yearlyInterest());

        while(running){
            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();

            if (choice < 1 || choice > 5) {
                System.out.println("Invalid menu choice.");
                continue;
            }
            MenuOption option = MenuOption.values()[choice - 1];

            String message = switch (option) {
                case OPEN_ACCOUNT -> {
                    openAccount(sc, accounts);
                    yield "Returning to main menu.";
                }
                case DEPOSIT ->
                        "Deposit - to be implemented in a later lab";
                case WITHDRAW ->
                        "Withdraw - to be implemented in a later lab";
                case TRANSFER ->
                        "Transfer - to be implemented in a later lab";
                case EXIT -> {
                    running = false;
                    yield "Thank you for using MiniBank!";
                }
            };
            System.out.println(message);
        }

        sc.close();
    }
    static void openAccount(Scanner sc, List<Account> accounts) {
        sc.nextLine();

        System.out.print("Enter owner name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Owner name cannot be empty.");
            return;
        }

        System.out.println("Choose account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        System.out.println("3. Fixed Deposit Account");
        System.out.print("Enter choice: ");

        if (!sc.hasNextInt()) {
            System.out.println("Invalid account type.");
            sc.nextLine();
            return;
        }

        int type = sc.nextInt();

        if (type < 1 || type > 3) {
            System.out.println("Invalid account type.");
            return;
        }

        System.out.print("Enter opening balance: ");

        if (!sc.hasNextLong()) {
            System.out.println("Please enter a valid amount.");
            sc.nextLine();
            return;
        }

        long balance = sc.nextLong();

        if (balance < 0) {
            System.out.println("Opening balance cannot be negative.");
            return;
        }

        Account account;

        switch (type) {
            case 1 -> {
                System.out.print("Enter minimum balance: ");
                if (!sc.hasNextLong()) {
                    System.out.println("Invalid minimum balance.");
                    sc.nextLine();
                    return;
                }
                long minimum = sc.nextLong();

                if (minimum < 0 || minimum > balance) {
                    System.out.println("Minimum balance must be between 0 and opening balance.");
                    return;
                }

                account = new SavingsAccount(name, balance, minimum);
            }
            case 2 -> {
                System.out.print("Enter overdraft limit: ");
                if (!sc.hasNextLong()) {
                    System.out.println("Invalid overdraft limit.");
                    sc.nextLine();
                    return;
                }
                long limit = sc.nextLong();

                if (limit < 0) {
                    System.out.println("Overdraft limit cannot be negative.");
                    return;
                }

                account = new CurrentAccount(name, balance, limit);
            }
            case 3 -> account = new FixedDepositAccount(name, balance);
            default -> {
                System.out.println("Invalid account type.");
                return;
            }
        }

        accounts.add(account);

        System.out.println("Account opened successfully!");
        System.out.println(account);
    }
    static void demonstrateConcurrency() {
        Account account = new SavingsAccount("Thread Test", 0, 0);

        int threadCount = 10;
        int times = 1000;

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(
                    new AccountWorker(account, times, 1),
                    "Bank-Worker-" + (i + 1)
            );

            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread execution interrupted.");
                return;
            }
        }

        System.out.println(
                "Final balance before synchronization: "
                        + account.getBalance()
        );
    }
    static void testTransactionProcessor() {
        Account account = new SavingsAccount("Test User", 10000, 1000);

        TransactionProcessor processor = new TransactionProcessor();

        processor.submit(new TransactionTask(account, true, 2000));
        processor.submit(new TransactionTask(account, false, 1000));
        processor.submit(new TransactionTask(account, true, 500));
        processor.submit(new TransactionTask(account, false, 500));

        processor.stop();

        System.out.println("Final balance: " + account.getBalance());
    }
}