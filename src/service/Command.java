package service;
public record Command(
        TransactionType type,
        String accountNumber,
        long amount
) {}
