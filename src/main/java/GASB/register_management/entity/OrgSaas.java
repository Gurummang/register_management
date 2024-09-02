package GASB.register_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "org_saas")
public class OrgSaas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "org_id", insertable = false, updatable = false)
    private Integer orgId;

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @Column (name = "saas_id", nullable = false)
    private Integer saasId;

    @Column (name = "space_id", unique = true)
    private String spaceId;    // workspace_id

    @Column(name = "status", nullable = false)
    private Integer status = 0; // default

    @Column (name = "security_score")
    private Integer securityScore = 0; // default

    @OneToMany(mappedBy = "orgSaas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelList> channels;

    @OneToOne(mappedBy = "orgSaas", cascade = CascadeType.ALL)
    private Workspace config;
}
