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
    private String cert;
    private String privateKey;
    private String publicKey;

    public DsUser(Integer id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public DsUser(String userName) {
        this.userName = userName;
    }

}
