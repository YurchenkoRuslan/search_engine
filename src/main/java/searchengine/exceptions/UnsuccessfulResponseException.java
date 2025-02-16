package searchengine.exceptions;

public class UnsuccessfulResponseException extends RuntimeException {
    private boolean result;

    public UnsuccessfulResponseException(String message) {
        super(message);
        result = false;
    }
}
