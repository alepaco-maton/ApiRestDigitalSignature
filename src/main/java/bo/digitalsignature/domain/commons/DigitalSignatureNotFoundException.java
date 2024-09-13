package bo.digitalsignature.domain.commons;

import lombok.Getter;

@Getter
public class DigitalSignatureNotFoundException extends Exception {

    private final String code;
    private final String message;

    public DigitalSignatureNotFoundException(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
