package GASB.register_management.repository;

import GASB.register_management.entity.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStatusRepository extends JpaRepository<FileStatus, Long> {
}
