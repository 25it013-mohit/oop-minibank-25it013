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
import java.nio.file.Files;
import java.nio.file.Path;
import service.StatePersister;
import service.ReportGenerator;

public class MiniBank {
    record BankInfo(String name, String branch){}
    enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        ACCOUNT_STATEMENT,
        LIST_ACCOUNTS,
        BATCH_PROCESSING,
        REPORT,
        EXIT
    }
    static class DemoResource implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Resource closed.");
        }
    }

    static void main(String[] args) {
        Command command = CommandParser.parse("DEPOSIT AC0001 500");

        System.out.println("Type: " + command.type());
        System.out.println("Account: " + command.accountNumber());
        System.out.println("Amount: " + command.amount());
        System.out.println("Mobile: " + Validator.isValidMobile("9876543210"));
        System.out.println("Email: " + Validator.isValidEmail("test@gmail.com"));
        System.out.println("PAN: " + Validator.isValidPan("ABCDE1234F"));
        System.out.println("IFSC: " + Validator.isValidIfsc("HDFC0001234"));

        System.out.println("Invalid mobile: " + Validator.isValidMobile("123"));
        System.out.println("Invalid email: " + Validator.isValidEmail("wrong-email"));
        SavingsAccount Testaccount = new SavingsAccount("Alice", 5000, 1000);

        System.out.println(StatementFormatter.buildStatement(Testaccount));
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
        BankService bankService = new BankService();
        Path dataFile = Path.of("data", "accounts.ser");
        try {
            if (Files.exists(dataFile)) {
                Account[] loadedAccounts = StatePersister.load(dataFile);

                for (Account account : loadedAccounts) {
                    accounts.add(account);
                    bankService.registerAccount(account);
                }

                System.out.println("Saved accounts loaded successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error loading saved accounts: " + e.getMessage());
        }
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
            System.out.println("5. Account Statement");
            System.out.println("6. List Accounts");
            System.out.println("7. Batch Transaction Processing");
            System.out.println("8. Report");
            System.out.println("9. Exit");
            System.out.println("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();

            if (choice < 1 || choice > 9) {
                System.out.println("Invalid menu choice.");
                continue;
            }
            MenuOption option = MenuOption.values()[choice - 1];

            String message = switch (option) {
                case OPEN_ACCOUNT -> {
                    openAccount(sc, accounts, bankService);
                    yield "Returning to main menu.";
                }
                case DEPOSIT -> {
                    deposit(sc, accounts, bankService);
                    yield "Returning to main menu.";
                }
                case WITHDRAW -> {
                    withdraw(sc, accounts, bankService);
                    yield "Returning to main menu.";
                }
                case TRANSFER -> {
                    transfer(sc, accounts, bankService);
                    yield "Returning to main menu.";
                }
                case ACCOUNT_STATEMENT -> {
                    sc.nextLine();

                    System.out.print("Enter account number: ");
                    String accountNumber = sc.nextLine().trim();

                    try {
                        System.out.println(bankService.statement(accountNumber));
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }

                    yield "Returning to main menu.";
                }

                case LIST_ACCOUNTS -> {
                    List<Account> allAccounts = bankService.listAccounts();

                    if (allAccounts.isEmpty()) {
                        System.out.println("No accounts found.");
                    } else {
                        for (Account account : allAccounts) {
                            System.out.println(
                                    account.getAccountNumber() + " | "
                                            + account.getOwnerName() + " | ₹"
                                            + account.getBalance()
                            );
                        }
                    }

                    yield "Returning to main menu.";
                }
                case BATCH_PROCESSING -> {
                    runBatchProcessing(sc, accounts);
                    yield "Returning to main menu.";
                }
                case REPORT -> {
                    Path logsFolder = Path.of("logs");
                    Path reportFile = Path.of("reports", "transaction_report.txt");

                    try {
                        ReportGenerator.generate(logsFolder, reportFile);
                        yield "Report generated successfully.";
                    } catch (java.io.IOException e) {
                        System.out.println("Error generating report: " + e.getMessage());
                        yield "Report generation failed.";
                    }
                }
                case EXIT -> {
                    running = false;
                    yield "Thank you for using MiniBank!";
                }
            };
            System.out.println(message);
        }

        sc.close();
        try {
            Files.createDirectories(dataFile.getParent());

            StatePersister.save(accounts.toArray(new Account[0]), dataFile);
            System.out.println("Accounts saved successfully.");

            Account[] loadedAccounts = StatePersister.load(dataFile);

            System.out.println("\nReloaded Accounts:");
            for (Account account : loadedAccounts) {
                System.out.println(
                        account.getAccountNumber() + " | "
                                + account.getOwnerName() + " | "
                                + account.getBalance()
                );
            }
            Path logsFolder = Path.of("logs");
            Path reportFile = Path.of("reports", "summary.txt");
            Files.createDirectories(reportFile.getParent());

            ReportGenerator.generate(logsFolder, reportFile);

        } catch (Exception e) {
            System.out.println("Persistence/report error: " + e.getMessage());
        }
    }
    static void openAccount(
            Scanner sc, List<Account> accounts, BankService bankService) {
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
        bankService.registerAccount(account);

        System.out.println("Account opened successfully!");
        System.out.println(account);
    }
    static void deposit(
            Scanner sc, List<Account> accounts, BankService bankService) {
        sc.nextLine();

        System.out.print("Enter account number: ");
        String accountNumber = sc.nextLine().trim();

        Account account = bankService.getAccount(accountNumber);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.print("Enter deposit amount: ");

        if (!sc.hasNextLong()) {
            System.out.println("Please enter a valid amount.");
            sc.nextLine();
            return;
        }

        long amount = sc.nextLong();

        try {
            bankService.deposit(accountNumber, amount);
            logTransaction("DEPOSIT", account, amount);

            System.out.println("Deposit successful.");
            System.out.println("Updated balance: ₹" + account.getBalance());

        } catch (BankException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }
    static void withdraw(
            Scanner sc, List<Account> accounts, BankService bankService) {
        sc.nextLine();

        System.out.print("Enter account number: ");
        String accountNumber = sc.nextLine().trim();

        Account account = bankService.getAccount(accountNumber);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.print("Enter withdrawal amount: ");

        if (!sc.hasNextLong()) {
            System.out.println("Please enter a valid amount.");
            sc.nextLine();
            return;
        }

        long amount = sc.nextLong();

        try {
            bankService.withdraw(accountNumber, amount);
            logTransaction("WITHDRAW", account, amount);

            System.out.println("Withdrawal successful.");
            System.out.println("Updated balance: ₹" + account.getBalance());

        } catch (BankException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }
    static void logTransaction(String type, Account account, long amount) {
        try {
            Path logFile = Path.of("logs", "transactions.log");
            Files.createDirectories(logFile.getParent());

            String entry = type + "," + account.getAccountNumber() + "," + amount;
            TransactionLog.append(logFile, entry);

        } catch (java.io.IOException e) {
            System.out.println("Warning: Transaction logging failed: "
                    + e.getMessage());
        }
    }
    static void transfer(
            Scanner sc, List<Account> accounts, BankService bankService) {
        sc.nextLine();

        System.out.print("Enter source account number: ");
        String sourceNumber = sc.nextLine().trim();

        Account source = bankService.getAccount(sourceNumber);

        if (source == null) {
            System.out.println("Source account not found.");
            return;
        }

        System.out.print("Enter destination account number: ");
        String destinationNumber = sc.nextLine().trim();

        Account destination = bankService.getAccount(destinationNumber);

        if (destination == null) {
            System.out.println("Destination account not found.");
            return;
        }

        if (source == destination) {
            System.out.println("Source and destination accounts must be different.");
            return;
        }

        System.out.print("Enter transfer amount: ");

        if (!sc.hasNextLong()) {
            System.out.println("Please enter a valid amount.");
            sc.nextLine();
            return;
        }

        long amount = sc.nextLong();

        try {
            bankService.transfer(sourceNumber, destinationNumber, amount);

            logTransaction("TRANSFER_OUT", source, amount);
            logTransaction("TRANSFER_IN", destination, amount);

            System.out.println("Transfer successful.");
            System.out.println("Source balance: ₹" + source.getBalance());
            System.out.println("Destination balance: ₹" + destination.getBalance());

        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
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
    public static void testAccountStore() {
        service.AccountStore store = new service.AccountStore();

        model.Account a1 = new model.SavingsAccount("Alice", 5000, 1000);
        model.Account a2 = new model.SavingsAccount("Bob", 2000, 500);
        model.Account a3 = new model.SavingsAccount("Charlie", 9000, 2000);

        store.add(a1);
        store.add(a2);
        store.add(a3);
        store.addTransaction("DEPOSIT,AC0001,1000");
        store.addTransaction("WITHDRAW,AC0002,500");

        System.out.println("\nTransaction history:");
        for (String transaction : store.getTransactionHistory()) {
            System.out.println(transaction);
        }

        System.out.println("Account lookup:");
        model.Account found = store.get(a2.getAccountNumber());
        System.out.println(found.getOwnerName() + " - " + found.getBalance());

        System.out.println("\nAccounts sorted by balance:");
        for (model.Account account : store.sortedByBalance()) {
            System.out.println(
                    account.getAccountNumber() + " - "
                            + account.getOwnerName() + " - "
                            + account.getBalance()
            );
        }
    }
    public static void testConcurrentAccountStore() {
        service.TransactionProcessor processor =
                new service.TransactionProcessor();

        model.Account account =
                new model.SavingsAccount("Test User", 5000, 1000);

        processor.addAccount(account);

        model.Account found =
                processor.getAccount(account.getAccountNumber());

        if (found != null) {
            System.out.println("Account found: " + found.getOwnerName());
            System.out.println("Balance: " + found.getBalance());
        } else {
            System.out.println("Account not found.");
        }

        processor.stop();
    }
    public static void testRepository() {
        service.Repository<model.Account> repository =
                new service.Repository<>();

        model.Account a1 =
                new model.SavingsAccount("Alice", 5000, 1000);

        model.Account a2 =
                new model.SavingsAccount("Bob", 2000, 500);

        model.Account a3 =
                new model.SavingsAccount("Charlie", 9000, 2000);

        repository.saveAll(a1, a2, a3);

        System.out.println("Account lookup:");
        model.Account found = repository.findById(a2.getAccountNumber());

        if (found != null) {
            System.out.println(found.getOwnerName() + " - " + found.getBalance());
        }

        System.out.println("\nAll repository accounts:");
        for (model.Account account : repository.findAll()) {
            System.out.println(
                    account.getAccountNumber() + " - "
                            + account.getOwnerName() + " - "
                            + account.getBalance()
            );
        }
        System.out.println("\nDeleting Alice's account:");
        boolean deleted = repository.delete(a1.getAccountNumber());
        System.out.println("Deleted: " + deleted);

        System.out.println("Account still exists: "
                + (repository.findById(a1.getAccountNumber()) != null));

        System.out.println("\nAverage balance: "
                + service.Repository.average(
                new Integer[]{5000, 2000, 9000}));
    }
    static void runBatchProcessing(Scanner sc, List<Account> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        service.TransactionProcessor processor =
                new service.TransactionProcessor();

        for (Account account : accounts) {
            processor.addAccount(account);
        }

        System.out.print("How many transactions do you want to process? ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            sc.nextLine();
            return;
        }

        int count = sc.nextInt();
        sc.nextLine();

        if (count <= 0) {
            System.out.println("Transaction count must be positive.");
            return;
        }

        for (int i = 0; i < count; i++) {
            System.out.println("\nTransaction " + (i + 1));
            System.out.print("Account number: ");
            String accountNumber = sc.nextLine().trim();

            Account account = processor.getAccount(accountNumber);

            if (account == null) {
                System.out.println("Account not found. Skipping transaction.");
                continue;
            }

            System.out.print("1. Deposit  2. Withdraw: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid transaction type. Skipping.");
                sc.nextLine();
                continue;
            }

            int type = sc.nextInt();

            System.out.print("Amount: ");

            if (!sc.hasNextLong()) {
                System.out.println("Invalid amount. Skipping.");
                sc.nextLine();
                continue;
            }

            long amount = sc.nextLong();
            sc.nextLine();

            if (type != 1 && type != 2) {
                System.out.println("Invalid transaction type. Skipping.");
                continue;
            }

            processor.submit(new TransactionTask(account, type == 1, amount));
        }

        processor.stop();

        System.out.println("\nBatch processing completed.");

        for (Account account : accounts) {
            System.out.println(
                    account.getAccountNumber() + " - "
                            + account.getOwnerName() + " - Balance: ₹"
                            + account.getBalance()
            );
        }
    }
}