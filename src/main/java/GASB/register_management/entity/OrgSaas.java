package GASB.register_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "org_saas")
public class OrgSaas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer orgSaasId;
    @Column(name = "org_id")
    private Integer orgId;
    @Column (name = "saas_id")
    private Integer saasId;
    @Column (name = "space_id")
    private String spaceId;    // workspace_id
    private Integer config;     // ref:> workspace_config.id
    private Integer status = 0; // default
    @Column (name = "sercurityScore")
    private Integer security_score = 0; // default
}
