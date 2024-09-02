package GASB.register_management.service.register;

import GASB.register_management.dto.register.AdminRequest;
import GASB.register_management.dto.register.AdminResponse;

import java.util.List;

public interface AdminService {
    AdminResponse registerAdmin(AdminRequest adminRequest);
    AdminResponse modifyAdmin(AdminRequest adminRequest);
    AdminResponse deleteAdmin(AdminRequest adminRequest);
    List<AdminResponse> getAdminList();
}
