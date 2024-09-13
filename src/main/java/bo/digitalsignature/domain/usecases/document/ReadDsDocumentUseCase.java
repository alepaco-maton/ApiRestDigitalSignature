package bo.digitalsignature.domain.usecases.document;

import bo.digitalsignature.domain.ports.IDsDocumentRepository;
import bo.digitalsignature.infrastructure.api.dto.dsuser.ListDsUserDsDocumentResponse;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class ReadDsDocumentUseCase {

    private IDsDocumentRepository repository;

    public List<ListDsUserDsDocumentResponse> findByDsUserId(Integer dsUserId) {
        return  repository.findByDsUserId(dsUserId);
    }

}
