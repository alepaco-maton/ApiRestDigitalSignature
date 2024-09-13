package bo.digitalsignature.infrastructure.api.dto.dsuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListDsUserResponse {

    private Integer id;
    private String fullName;
    private List<ListDsUserDsDocumentResponse> documents;

}
