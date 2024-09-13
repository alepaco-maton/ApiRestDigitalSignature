package bo.digitalsignature.domain.usecases.user;

import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import bo.digitalsignature.domain.usecases.pairKeys.CreatePairKeyUseCase;
import bo.digitalsignature.domain.usecases.user.validator.CreateDsUserValidator;
import bo.digitalsignature.infrastructure.api.exception.DigitalSignatureException;
import bo.digitalsignature.domain.commons.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateDsUserUseCase {

    private IMultiLanguageMessagesService mlms;
    private IDsUserRepository repository;
    private CreateDsUserValidator validator;
    private CreatePairKeyUseCase createPairKeyUseCase;

    public DsUser create(DsUser dsUser)  throws DigitalSignatureException {
        ErrorCode errorCode = validator.validate(dsUser);

        if (!errorCode.isSuccessfull()) {
            throw new DigitalSignatureException(errorCode.getCode(),
                    mlms.getMessage(errorCode.getCode()));
        }

        dsUser = this.repository.save(dsUser);
        dsUser = createPairKeyUseCase.create(dsUser);
        dsUser = this.repository.update(dsUser);

        return dsUser;
    }

}
