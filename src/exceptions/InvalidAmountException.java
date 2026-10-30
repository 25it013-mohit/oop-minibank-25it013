package exception;

public class InvalidAmountException extends exception.BankException {

    public InvalidAmountException(String message) {
        super(message);
    }
}
