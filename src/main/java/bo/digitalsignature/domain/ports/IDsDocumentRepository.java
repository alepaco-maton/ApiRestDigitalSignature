package bo.digitalsignature.domain.ports;

import bo.digitalsignature.domain.entities.DsDocument;
import bo.digitalsignature.infrastructure.api.dto.dsuser.ListDsUserDsDocumentResponse;
import bo.digitalsignature.infrastructure.persistence.entity.DsDocumentEntity;

import java.util.List;

public interface IDsDocumentRepository {

    List<ListDsUserDsDocumentResponse> findByDsUserId(Integer dsUserId);

    void deleteAllByDsUserId(int id);

    DsDocument save(DsDocument model);

    DsDocumentEntity findEntityById(Integer id);

}
