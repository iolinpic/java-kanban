package exceptions;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(Exception e) {
        super(e);
    }
}
