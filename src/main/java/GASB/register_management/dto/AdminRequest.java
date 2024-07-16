package GASB.register_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRequest {

    private Integer org_id;
    private String email;
    private String password;
    private String first_name;
    private String last_name;
}
