package GASB.register_management.repository;

import GASB.register_management.entity.Saas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaasRepository extends JpaRepository<Saas, Integer> {
}
