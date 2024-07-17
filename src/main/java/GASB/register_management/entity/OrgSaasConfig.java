package GASB.register_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "saas_config")
public class OrgSaasConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String saas_admin_email;
    private String api_key;
    private String webhook_url;
    private String validation = "default";
    private String saas_alias;
    private Timestamp register_date;
}
