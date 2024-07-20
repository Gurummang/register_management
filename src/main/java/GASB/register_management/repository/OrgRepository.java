package GASB.register_management.repository;

import GASB.register_management.entity.Org;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgRepository extends JpaRepository<Org, Integer> {
}
