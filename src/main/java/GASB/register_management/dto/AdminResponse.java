package GASB.register_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminResponse {

    // POST
    private String status;          // 성공 or 실패
    private String message;         // 상세 메시지
    // POST & GET
    private Integer id;             // admin_id
    // GET
    private Integer org_id;
    private String email;
    private String name;            // first_name + last_name
    private Timestamp last_login;   // default == 최초 등록 시간?

    // POST (register, delete, modify)
    public AdminResponse(String status, String message, Integer id) {
        this.status = status;
        this.message = message;
        this.id = id;
    }
    // GET
//    public AdminResponse(Integer id, Integer org_id, String email, String name, Timestamp last_login) {
//        this.id = id;
//        this.org_id = org_id;
//        this.email = email;
//        this.name = name;
//        this.last_login = last_login;
//    }

    public AdminResponse(Integer id, Integer org_id, String email, String name, java.sql.Timestamp last_login) {
        this.id = id;
        this.org_id = org_id;
        this.email = email;
        this.name = name;
        this.last_login = last_login;
    }
}
