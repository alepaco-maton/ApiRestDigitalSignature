package bo.digitalsignature.infrastructure.api.dto.dsdocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignedDocumentResponse {

    private Integer id;
    private String fileName;

}
