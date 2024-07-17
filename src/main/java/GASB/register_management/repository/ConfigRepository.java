package GASB.register_management.repository;

import GASB.register_management.entity.OrgSaas;
import GASB.register_management.entity.OrgSaasConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgSaasConfigRepository extends JpaRepository<OrgSaasConfig, Long> {
}
