package GASB.register_management.service.user;

import GASB.register_management.dto.user.DlpTop5;
import GASB.register_management.dto.user.LastActivities;
import GASB.register_management.dto.user.MalwareTop5;
import GASB.register_management.dto.user.UserStatisticsDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserStatisticsService {

    public UserStatisticsDto getStatistics(long orgId){
        return UserStatisticsDto.builder()
                .lastActivities(getLastActivities())
                .topSensitive(getDlpTop5())
                .topMalware(malwareTop5())
                .build();
    }

    public LastActivities getLastActivities(){
        return LastActivities.builder()
                .dormant(20)
                .domanting(50)
                .undormant(11)
                .build();
    }

    public List<DlpTop5> getDlpTop5(){
        // DlpTop5에 대한 목업 데이터 생성
        return Arrays.asList(
                new DlpTop5("User1", 15),
                new DlpTop5("User2", 10),
                new DlpTop5("User3", 8),
                new DlpTop5("User4", 5),
                new DlpTop5("User5", 3)
        );
    }

    public List<MalwareTop5> malwareTop5(){
        // MalwareTop5에 대한 목업 데이터 생성
        return Arrays.asList(
                new MalwareTop5("User1", 7),
                new MalwareTop5("User2", 5),
                new MalwareTop5("User3", 4),
                new MalwareTop5("User4", 3),
                new MalwareTop5("User5", 2)
        );
    }
}
