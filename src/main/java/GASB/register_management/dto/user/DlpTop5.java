package GASB.register_management.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DlpTop5 {

    private String user;
    private int sensitive;

    public DlpTop5(String user, int sensitive){
        this.user = user;
        this.sensitive = sensitive;
    }
}
