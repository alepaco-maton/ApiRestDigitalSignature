package bo.digitalsignature.domain.usecases.user.businessrules;

import bo.digitalsignature.domain.commons.AppTools;
import bo.digitalsignature.domain.commons.IValidator;
import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DsUserUserNameValidator implements IValidator<DsUser> {

    public static final int USER_NAME_MIN_LENGHT = 3;
    public static final int USER_NAME_MAX_LENGHT = 50;

    private IDsUserRepository repository;

    @Override
    public ErrorCode validate(DsUser dsUser) {
        if (AppTools.isBlank(dsUser.getUserName())) {
            return ErrorCode.CREATE_DS_USER_USER_NAME_IS_REQUIRED;
        }

        int length = dsUser.getUserName().trim().length();

        if (length < USER_NAME_MIN_LENGHT || length > USER_NAME_MAX_LENGHT) {
            return ErrorCode.CREATE_DS_USER_USER_NAME_IS_INVALID;
        }

        if (!repository.list(dsUser.getUserName()).isEmpty()) {
            return ErrorCode.CREATE_DS_USER_USER_NAME_ALREADY_EXIST;
        }

        return ErrorCode.SUCCESSFUL;
    }

}
