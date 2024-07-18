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
    private Integer org_id;
    private Integer saas_id;
    private String space_id;    // workspace_id
    private Integer config;     // ref:> workspace_config.id
    private Integer status = 0; // default
    private Integer security_score = 0; // default
}
