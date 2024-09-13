package bo.digitalsignature.domain.ports;

import bo.digitalsignature.domain.entities.DsUser;

import java.util.List;

public interface IDsUserRepository {

    DsUser save(DsUser dsUser);

    DsUser update(DsUser dsUser);

    List<DsUser> list(String userName);

    DsUser findById(Integer id);

}
