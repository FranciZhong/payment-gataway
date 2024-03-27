package team.starfish.paymentgateway.error;

public class ExternalApiException extends Exception {

    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
