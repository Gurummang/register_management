package GASB.register_management.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserStatisticsDto {

    private LastActivities lastActivities;
    private List<DlpTop5> topSensitive;
    private List<MalwareTop5> topMalware;
}
