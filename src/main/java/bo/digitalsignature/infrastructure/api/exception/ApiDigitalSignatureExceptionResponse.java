package bo.digitalsignature.infrastructure.api.exception;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiDigitalSignatureExceptionResponse implements Serializable {

    private static final long serialVersionUID = 1321060619595537832L;

    private HttpStatus status;
    private String code;
    private String message;

}
