package GASB.register_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "workspace_config")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;         // config_id == org_saas_space_id?
    private String workspace_name;
    private String saas_admin_email;
    private String token;       // api_key, api_token
    private String webhook; // webhook은 workspace당 한개로!
    private String validation = "[default]";
    private String alias;
    private Timestamp register_date;
}
