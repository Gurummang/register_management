package GASB.register_management.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserInfo {
    private long id;
    private String saas;
    private String user;
    private String account;
    private int totalUpload;
    private int sensitive;
    private int malware;
    private LocalDateTime lastDate;
}
