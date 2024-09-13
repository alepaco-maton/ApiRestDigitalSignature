package bo.digitalsignature.domain.usecases.user.validator;

import bo.digitalsignature.domain.commons.IValidator;
import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.usecases.user.businessrules.DsUserUserNameValidator;
import bo.digitalsignature.domain.commons.ErrorCode;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CreateDsUserValidator {

    private IDsUserRepository repository;

    public ErrorCode validate(DsUser dsUser) {
        List<IValidator<DsUser>> validators = new ArrayList<>();
        validators.add(new DsUserUserNameValidator(repository));

        for (IValidator val : validators) {
            ErrorCode errorCode = val.validate(dsUser);

            if (!errorCode.isSuccessfull()) {
                return errorCode;
            }
        }

        return ErrorCode.SUCCESSFUL;
    }
}
