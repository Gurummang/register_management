package GASB.register_management.repository;

import GASB.register_management.entity.ChannelList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelListRepository extends JpaRepository<ChannelList, Long> {
}
