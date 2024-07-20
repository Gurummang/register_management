package GASB.register_management.service.imple;

import GASB.register_management.dto.OrgRequest;
import GASB.register_management.dto.OrgResponse;
import GASB.register_management.entity.Org;
import GASB.register_management.repository.OrgRepository;
import GASB.register_management.service.OrgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrgServiceImple implements OrgService {

    @Autowired
    private OrgRepository orgRepository;

    @Override
    public OrgResponse registerOrg(OrgRequest orgRequest) {
        Org org = new Org();
        org.setOrg_name(orgRequest.getOrg_name());
        Org saveOrg = orgRepository.save(org);
        
        return new OrgResponse("success", "Register Success: " + saveOrg.getOrg_name(), saveOrg.getId());
    }
    
    @Override
    public OrgResponse modifyOrg(OrgRequest orgRequest) {
        Optional<Org> optionalOrg = orgRepository.findById(orgRequest.getId());
        
        if(optionalOrg.isPresent()) {
            Org org = optionalOrg.get();
            org.setOrg_name(orgRequest.getOrg_name());
            Org updatedOrg = orgRepository.save(org);
            
            return new OrgResponse ("success", "Modify Success: " + updatedOrg.getOrg_name(), updatedOrg.getId());
        }
        else {
            return new OrgResponse ("failure", "Org not found for ID: " + orgRequest.getId(), null);
        }
    }

    @Override
    public OrgResponse deleteOrg(Integer id) {
        Optional<Org> optionalOrg = orgRepository.findById(id);

        if (optionalOrg.isPresent()) {
            orgRepository.deleteById(id);
            return new OrgResponse("success", "Delete Success for ID: " + id, id);
        } else {
            return new OrgResponse("failure", "Org not found for ID: " + id, null);
        }
    }

    @Override
    public List<OrgResponse> getOrgList() {
        return orgRepository.findAll().stream()
                .map(org -> new OrgResponse(org.getId(), org.getOrg_name()))
                .collect(Collectors.toList());
    }
}
