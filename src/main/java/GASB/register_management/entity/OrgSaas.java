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
    private Integer config_file;
    private Integer status;
    private Integer security_score;
}
