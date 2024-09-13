package bo.digitalsignature.domain.usecases.user.businessrules;

import bo.digitalsignature.domain.commons.AppTools;
import bo.digitalsignature.domain.commons.IValidator;
import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import lombok.AllArgsConstructor;

import java.util.List;

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

        List<DsUser> list = repository.list(dsUser.getUserName());

        if (!list.isEmpty()) {
            if(dsUser.getId() == null) {
                return ErrorCode.CREATE_UPDATE_DS_USER_USER_NAME_ALREADY_EXIST;
            } else {
                if(!list.get(0).getId().equals(dsUser.getId())) {
                    return ErrorCode.CREATE_UPDATE_DS_USER_USER_NAME_ALREADY_EXIST;
                }
            }
        }

        return ErrorCode.SUCCESSFUL;
    }

}
