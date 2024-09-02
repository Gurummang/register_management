package GASB.register_management.service.user;

import GASB.register_management.dto.user.UserTotalDto;
import GASB.register_management.repository.MonitoredUsersRepo;
import org.springframework.stereotype.Service;

@Service
public class UserTotalService {

    private final MonitoredUsersRepo monitoredUsersRepo;

    public UserTotalService(MonitoredUsersRepo monitoredUsersRepo){
        this.monitoredUsersRepo = monitoredUsersRepo;
    }

    public UserTotalDto getTotal(long orgId){
        return UserTotalDto.builder()
                .total(getUserTotal(orgId))
                .dormantTotal(getDormantTotal(orgId))
                .dlpTotal(getDlpTotal(orgId))
                .malwareTotal(getMalwareTotal(orgId))
                .build();
    }

    private int getUserTotal(long orgId){
        return monitoredUsersRepo.getTotalUserCount(orgId);
    }

    private int getDormantTotal(long orgId){
        return 0;
    }

    private int getDlpTotal(long orgId){
        return monitoredUsersRepo.countDistinctUsersWithSensitiveActivity(orgId);
    }

    private int getMalwareTotal(long orgId){
        return monitoredUsersRepo.countDistinctUsersWithSuspiciousActivity(orgId);
    }
}
