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
                case OPEN_ACCOUNT ->
                        "Open Account - to be implemented in a later lab";
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