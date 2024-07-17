package GASB.register_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "workspace_config")
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String workspace_name;
    private String saas_admin_email;
    private String token;       // api_key, api_token
    private String webhook_url; // webhook은 workspace당 한개로!
    private String validation = "[default]";
    private String saas_alias;
    private Timestamp register_date;
}
