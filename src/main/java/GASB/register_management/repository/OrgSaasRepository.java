package GASB.register_management.repository;

import GASB.register_management.entity.OrgSaas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgSaasRepository extends JpaRepository<OrgSaas, Integer> {
    List<OrgSaas> findByConfig(Long configId);

    @Query("SELECT os, w FROM OrgSaas os JOIN Workspace w ON os.config = w.id WHERE os.org_id = :orgId")
    List<Object[]> findByOrgId(@Param("orgId") Integer orgId);
}

