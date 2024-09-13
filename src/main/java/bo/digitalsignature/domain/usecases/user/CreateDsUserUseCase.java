package bo.digitalsignature.domain.usecases.user;

import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import bo.digitalsignature.domain.usecases.cypher.CreateCertAndPairKeyUseCase;
import bo.digitalsignature.domain.usecases.user.validator.CreateDsUserValidator;
import bo.digitalsignature.domain.commons.DigitalSignatureException;
import bo.digitalsignature.domain.commons.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateDsUserUseCase {

    private IMultiLanguageMessagesService mlms;
    private IDsUserRepository repository;
    private CreateDsUserValidator validator;
    private CreateCertAndPairKeyUseCase createCertAndPairKeyUseCase;

    public DsUser create(DsUser dsUser, String pathFolderByUser)  throws DigitalSignatureException {
        ErrorCode errorCode = validator.validate(dsUser);

        if (!errorCode.isSuccessfull()) {
            throw new DigitalSignatureException(errorCode.getCode(),
                    mlms.getMessage(errorCode.getCode()));
        }

        dsUser = this.repository.save(dsUser);
        dsUser = createCertAndPairKeyUseCase.create(dsUser, pathFolderByUser);
        dsUser = this.repository.update(dsUser);

        return dsUser;
    }

}
