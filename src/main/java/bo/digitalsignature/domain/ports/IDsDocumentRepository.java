package bo.digitalsignature.domain.ports;

import bo.digitalsignature.infrastructure.api.dto.dsuser.ListDsUserDsDocumentResponse;
import java.util.List;

public interface IDsDocumentRepository {

    List<ListDsUserDsDocumentResponse> findByDsUserId(Integer dsUserId);

}
