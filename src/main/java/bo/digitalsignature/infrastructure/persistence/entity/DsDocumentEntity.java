package bo.digitalsignature.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DsDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ds_user_id", referencedColumnName = "id")
    private DsUserEntity user;
    private String fileName;
    private String path;

}
