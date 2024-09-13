package bo.digitalsignature.domain.usecases.user.validator;

import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteDsUserValidator {

    private IDsUserRepository repository;

    public ErrorCode validate(int id) {
        DsUser model = this.repository.findById(id);

        if(model == null) {
            return ErrorCode.DELETE_DS_USER_ID_NOT_FOUND;
        }

        return ErrorCode.SUCCESSFUL;
    }
}
