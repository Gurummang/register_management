package GASB.register_management.repository;

import GASB.register_management.entity.OrgSaas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgSaasRepository extends JpaRepository<OrgSaas, Integer> {
    List<OrgSaas> findByConfig(int config);

    List<OrgSaas> findByOrgId(Integer orgId);
}

