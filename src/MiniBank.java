import model.Account;
import model.*;
import service.*;
import util.*;
import static util.Validator.isValidEmail;
import java.util.Scanner;

public class MiniBank {
    record BankInfo(String name, String branch){}
    enum MenuOption{
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }

    static void main(String[] args) {
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
        System.out.println("Email valid: " + isValidEmail("abc@xyz.com"));

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