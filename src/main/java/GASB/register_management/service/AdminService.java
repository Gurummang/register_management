package GASB.register_management.service;

import GASB.register_management.dto.AdminRequest;
import GASB.register_management.dto.AdminResponse;

import java.util.List;

public interface AdminService {

    AdminResponse registerAdmin(AdminRequest adminRequest);
    AdminResponse modifyAdmin(AdminRequest adminRequest);
    AdminResponse deleteAdmin(AdminRequest adminRequest);
    List<AdminResponse> getAdminList();
}
