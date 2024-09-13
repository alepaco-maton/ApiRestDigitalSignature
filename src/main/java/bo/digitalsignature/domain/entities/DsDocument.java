package bo.digitalsignature.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DsDocument {

    private Integer id;
    private Integer dsUserId;
    private String fileName;
    private String path;

}
