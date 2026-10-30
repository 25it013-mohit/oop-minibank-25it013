package service;

import java.nio.file.Path;

public class TransactionLogDemo {

    public static void main(String[] args) throws Exception {
        Path logFile = Path.of("logs", "transactions.log");

        TransactionLog.append(logFile, "DEPOSIT,AC0001,1000");
        TransactionLog.append(logFile, "WITHDRAW,AC0001,300");

        System.out.println("Transactions logged successfully.");
    }
}
