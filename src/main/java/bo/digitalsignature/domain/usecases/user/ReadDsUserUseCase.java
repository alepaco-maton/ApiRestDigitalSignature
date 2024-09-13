package bo.digitalsignature.domain.usecases.user;

import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class ReadDsUserUseCase {

    private IDsUserRepository repository;

    public List<DsUser> list(String fullName) {
        return repository.list(fullName);
    }

}
