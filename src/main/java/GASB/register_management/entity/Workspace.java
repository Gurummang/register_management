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
    private Integer id;
    @Column(name = "workspace_name")
    private String spaceName;
    @Column(name = "saas_admin_email")
    private String adminEmail;
    @Column(name = "token", length = 1024)
    private String apiToken;       // api_key, api_token
    @Column(name = "refresh_token")
    private String refreshToken;
    @Column(name = "webhook")
    private String webhookUrl; // webhook은 workspace당 한개로!
    private String alias = "NULL";
    @Column (name = "register_date")
    private Timestamp registerDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private OrgSaas orgSaas;
}
