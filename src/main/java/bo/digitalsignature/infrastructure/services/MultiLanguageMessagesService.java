package bo.digitalsignature.infrastructure.services;

import java.util.Locale;

import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MultiLanguageMessagesService implements IMultiLanguageMessagesService {

    @Autowired
    private MessageSource messageSource;


    public String getMessage(String code, String[] args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }
}
