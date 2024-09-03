package GASB.register_management.service.user;

import GASB.register_management.dto.user.UserTotalDto;
import GASB.register_management.entity.MonitoredUsers;
import GASB.register_management.repository.ActivitiesRepository;
import GASB.register_management.repository.MonitoredUsersRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class UserTotalService {

    private final MonitoredUsersRepo monitoredUsersRepo;
    private final ActivitiesRepository activitiesRepository;

    public UserTotalService(MonitoredUsersRepo monitoredUsersRepo, ActivitiesRepository activitiesRepository){
        this.monitoredUsersRepo = monitoredUsersRepo;
        this.activitiesRepository = activitiesRepository;
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

    private int getDormantTotal(long orgId) {
        // 모든 사용자를 가져옵니다.
        List<MonitoredUsers> users = monitoredUsersRepo.getUserListByOrgId(orgId);

        // 현재 날짜를 구합니다.
        LocalDateTime currentDate = LocalDateTime.now();

        // 휴면 계정 수를 카운트할 변수
        int dormantCount = 0;

        // 각 사용자에 대해 마지막 활동 시간을 확인하고, 휴면 여부를 검사합니다.
        for (MonitoredUsers user : users) {
            LocalDateTime lastActiveDate = getLastDate(user.getId());

            if (lastActiveDate == null) {
                // 마지막 활동 시간이 없는 경우, 휴면 계정으로 간주
                dormantCount++;
            } else {
                // 마지막 활동 날짜와 현재 날짜 간의 개월 수를 계산합니다.
                long monthsSinceLastActive = ChronoUnit.MONTHS.between(lastActiveDate, currentDate);

                // 활동하지 않은 지 12개월 이상이면 휴면 계정으로 간주
                if (monthsSinceLastActive >= 12) {
                    dormantCount++;
                }
            }
        }

        return dormantCount; // 휴면 계정 수 반환
    }


    private int getDlpTotal(long orgId){
        return monitoredUsersRepo.countDistinctUsersWithSensitiveActivity(orgId);
    }

    private int getMalwareTotal(long orgId){
        return monitoredUsersRepo.countDistinctUsersWithSuspiciousActivity(orgId);
    }

    private LocalDateTime getLastDate(long userId){
        return activitiesRepository.findLastActiveTime(userId);
    }

}
