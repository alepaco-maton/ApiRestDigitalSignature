package bo.digitalsignature.infrastructure.api.exception;

import java.net.ConnectException;

import bo.digitalsignature.domain.commons.DigitalSignatureException;
import bo.digitalsignature.domain.commons.DigitalSignatureNotFoundException;
import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.infrastructure.services.MultiLanguageMessagesService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author alepaco.com
 */
@Log4j2
@Component
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    MultiLanguageMessagesService mlms;

    @ExceptionHandler({DigitalSignatureException.class})
    public ResponseEntity<Object> mihandleAll(Exception ex, WebRequest request) {
        DigitalSignatureException exception = (DigitalSignatureException) ex;

        ResponseEntity<Object> out = new ResponseEntity<>(
                new ApiDigitalSignatureExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY, exception.getCode(),
                        exception.getMessage()),
                HttpHeaders.EMPTY, HttpStatus.UNPROCESSABLE_ENTITY);

        return out;
    }

    @ExceptionHandler({DigitalSignatureNotFoundException.class})
    public ResponseEntity<Object> mihandleNotFound(Exception ex, WebRequest request) {
        DigitalSignatureNotFoundException exception = (DigitalSignatureNotFoundException) ex;

        ResponseEntity<Object> out = new ResponseEntity<>(
                new ApiDigitalSignatureExceptionResponse(HttpStatus.NOT_FOUND, exception.getCode(),
                        exception.getMessage()),
                HttpHeaders.EMPTY, HttpStatus.NOT_FOUND);

        return out;
    }

    @ExceptionHandler({Exception.class, ConnectException.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        ResponseEntity<Object> out = new ResponseEntity<>(
                new ApiDigitalSignatureExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY,
                        ErrorCode.ERROR_PROCESSING_THE_TRANSACTION.getCode(),
                        mlms.getMessage(ErrorCode.ERROR_PROCESSING_THE_TRANSACTION.getCode())),
                HttpHeaders.EMPTY, HttpStatus.UNPROCESSABLE_ENTITY);

        log.error(ex.getCause(), ex);

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE - HTTP STATUS : " + HttpStatus.UNPROCESSABLE_ENTITY + " ----------------------\n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }

}
