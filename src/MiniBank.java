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
        Account negativeAccount = new SavingsAccount("Test", -500, 100);

        List<String> errors = AnnotationValidator.validate(negativeAccount);

        errors.forEach(System.out::println);
        BankInfo bank = new BankInfo(
                "MiniBank",
                "CHARUSAT"
        );

        Scanner sc = new Scanner(System.in);
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
            System.out.println("\n1. Open model.Account");
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
                case OPEN_ACCOUNT ->
                        "Open model.Account - to be implemented in a later lab";
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
}