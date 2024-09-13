package bo.digitalsignature.domain.usecases.user;

import bo.digitalsignature.domain.commons.DigitalSignatureException;
import bo.digitalsignature.domain.commons.DigitalSignatureNotFoundException;
import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import bo.digitalsignature.domain.usecases.user.validator.UpdateDsUserValidator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateDsUserUseCase {

    private IMultiLanguageMessagesService mlms;
    private IDsUserRepository repository;
    private UpdateDsUserValidator validator;

    public void update(DsUser dsUser) throws DigitalSignatureException,
            DigitalSignatureNotFoundException {
        ErrorCode errorCode = validator.validate(dsUser);

        if (!errorCode.isSuccessfull()) {
            if (errorCode.getCode().equals(ErrorCode.UPDATE_DS_USER_ID_NOT_FOUND.getCode())) {
                throw new DigitalSignatureNotFoundException(errorCode.getCode(),
                        mlms.getMessage(errorCode.getCode()));
            }

            throw new DigitalSignatureException(errorCode.getCode(),
                    mlms.getMessage(errorCode.getCode()));
        }

        DsUser model = repository.findById(dsUser.getId());
        model.setUserName(dsUser.getUserName());
        repository.update(model);
    }

}
