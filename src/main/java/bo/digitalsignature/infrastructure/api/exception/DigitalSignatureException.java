package bo.digitalsignature.infrastructure.api.exception;

import lombok.Getter;

@Getter
public class DigitalSignatureException extends Exception {

    private final String code;
    private final String message;

    public DigitalSignatureException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public DigitalSignatureException(String code, String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }

}
