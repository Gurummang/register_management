package GASB.register_management.controller.register;

import GASB.register_management.dto.register.AdminRequest;
import GASB.register_management.dto.register.AdminResponse;
import GASB.register_management.service.register.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/register")
    public AdminResponse registerAdmin(@RequestBody AdminRequest adminRequest) {
        return adminService.registerAdmin(adminRequest);
    }

    @PostMapping("/modify")
    public AdminResponse modifyAdmin(@RequestBody AdminRequest adminRequest) {
        return adminService.modifyAdmin(adminRequest);
    }

    @PostMapping("/delete")
    public AdminResponse deleteAdmin(@RequestBody AdminRequest adminRequest) {
        return adminService.deleteAdmin(adminRequest);
    }

    @GetMapping
    public List<AdminResponse> getAdminList() {
        return adminService.getAdminList();
    }
}
