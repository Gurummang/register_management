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
    private Integer id;
    @Column(name = "org_id")
    private Integer orgId;
    @Column (name = "saas_id")
    private Integer saasId;
    @Column (name = "space_id")
    private String spaceId;    // workspace_id
    private Integer status = 0; // default
    @Column (name = "security_score")
    private Integer securityScore = 0; // default
}
