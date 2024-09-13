package bo.digitalsignature.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DsUser {

    private Integer id;
    private String userName;
    private String privateKey;
    private String publicKey;

    public DsUser(String userName) {
        this.userName = userName;
    }

}
