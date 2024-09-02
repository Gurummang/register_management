package GASB.register_management.service.imple;

import GASB.register_management.dto.register.AdminRequest;
import GASB.register_management.dto.register.AdminResponse;
import GASB.register_management.entity.Admin;
import GASB.register_management.repository.AdminRepository;
import GASB.register_management.service.register.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImple implements AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminServiceImple(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public AdminResponse registerAdmin(AdminRequest adminRequest) {
        Admin admin = new Admin();
        admin.setOrg_id(adminRequest.getOrg_id());
        admin.setEmail(adminRequest.getEmail());
        admin.setPassword(adminRequest.getPassword());
        admin.setFirst_name(adminRequest.getFirst_name());
        admin.setLast_name(adminRequest.getLast_name());
        admin.setLast_login(Timestamp.valueOf(LocalDateTime.now()));
        Admin savedAdmin = adminRepository.save(admin);

        return new AdminResponse("success", "Register Success: " + savedAdmin.getFirst_name() + " " + savedAdmin.getLast_name(), savedAdmin.getId());
    }

    @Override
    public AdminResponse modifyAdmin(AdminRequest adminRequest) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminRequest.getId());

        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();

            admin.setOrg_id(adminRequest.getOrg_id());
            admin.setEmail(adminRequest.getEmail());
            admin.setPassword(adminRequest.getPassword());
            admin.setFirst_name(adminRequest.getFirst_name());
            admin.setLast_name(adminRequest.getLast_name());
            admin.setLast_login(Timestamp.valueOf(LocalDateTime.now()));
            Admin updatedAdmin = adminRepository.save(admin);

            return new AdminResponse("success", "Modify Success: " + updatedAdmin.getFirst_name() + " " + updatedAdmin.getLast_name(), updatedAdmin.getId());

        } else {
            return new AdminResponse("failure", "Admin not found for ID: " + adminRequest.getId(), null);
        }
    }

    @Override
    public AdminResponse deleteAdmin(AdminRequest adminRequest) {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminRequest.getId());

        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            if (admin.getEmail().equals(adminRequest.getEmail()) && admin.getPassword().equals(adminRequest.getPassword())) {
                adminRepository.deleteById(adminRequest.getId());
                return new AdminResponse("success", "Delete Success for ID: " + adminRequest.getId(), adminRequest.getId());
            } else {
                return new AdminResponse("failure", "Email or password do not match for ID: " + adminRequest.getId(), null);
            }
        } else {
            return new AdminResponse("failure", "Admin not found for ID: " + adminRequest.getId(), null);
        }
    }

    @Override
    public List<AdminResponse> getAdminList() {
        return adminRepository.findAll().stream()
                .map(admin -> new AdminResponse(admin.getId(), admin.getOrg_id(),admin.getEmail(),admin.getFirst_name()+" "+admin.getLast_name(), admin.getLast_login()))
                .collect(Collectors.toList());
    }
}
