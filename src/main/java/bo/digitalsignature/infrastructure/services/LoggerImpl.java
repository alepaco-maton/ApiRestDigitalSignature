package bo.digitalsignature.infrastructure.services;

import bo.digitalsignature.domain.ports.ILogger;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class LoggerImpl implements ILogger {

    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void info(String message, Throwable exception) {
        log.info(message, exception);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void warn(String message, Throwable exception) {
        log.warn(message, exception);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public void error(String message, Throwable exception) {
        log.error(message, exception);
    }
}
