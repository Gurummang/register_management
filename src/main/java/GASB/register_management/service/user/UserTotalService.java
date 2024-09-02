package GASB.register_management.service.user;

import GASB.register_management.dto.user.UserTotalDto;
import org.springframework.stereotype.Service;

@Service
public class UserTotalService {

    public UserTotalDto getTotal(){
        return UserTotalDto.builder()
                .total(2024)
                .dormantTotal(11)
                .dlpTotal(3)
                .malwareTotal(5)
                .build();
    }
}
