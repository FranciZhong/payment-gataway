package team.starfish.paymentgateway.error;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
