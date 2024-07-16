package GASB.register_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRequest {

    private Integer id;
    private Integer org_id;
    private String first_name;
    private String last_name;

    // For Modify
    private String cur_email;
    private String cur_password;
    // For Regi, Modify, Delete
    private String email;
    private String password;
}
