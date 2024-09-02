package GASB.register_management.repository;

import GASB.register_management.entity.MonitoredUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredUsersRepo extends JpaRepository<MonitoredUsers, Long> {
}
