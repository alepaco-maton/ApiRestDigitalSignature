package bo.digitalsignature.infrastructure.api.dto.dsuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDsUserResponse {

    private Integer id;
    private String userName;

}
