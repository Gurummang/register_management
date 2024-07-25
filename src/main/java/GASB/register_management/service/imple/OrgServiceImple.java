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
        org.setOrgName(orgRequest.getOrgName());
        Org regiOrg = orgRepository.save(org);
        
        return new OrgResponse(200, null, regiOrg.getId(), null);
    }
    
    @Override
    public OrgResponse modifyOrg(OrgRequest orgRequest) {
        Optional<Org> optionalOrg = orgRepository.findById(orgRequest.getId());
        
        if(optionalOrg.isPresent()) {
            Org org = optionalOrg.get();
            org.setOrgName(orgRequest.getOrgName());
            Org updatedOrg = orgRepository.save(org);
            
            return new OrgResponse(200, null, updatedOrg.getId(), updatedOrg.getOrgName());
        }
        else {
            return new OrgResponse(199, "Not fount for Id" + orgRequest.getId(), null, null);
        }
    }

    @Override
    public OrgResponse deleteOrg(Integer id) {
        Optional<Org> optionalOrg = orgRepository.findById(id);

        if (optionalOrg.isPresent()) {
            orgRepository.deleteById(id);
            return new OrgResponse(200, null, optionalOrg.get().getId(), null);
        } else {
            return new OrgResponse(199, "Not fount for Id" + id, null, null);
        }
    }

    @Override
    public List<OrgResponse> getOrgList() {
        return orgRepository.findAll().stream()
                .map(org -> new OrgResponse(null, null, org.getId(), org.getOrgName()))
                .collect(Collectors.toList());
    }
}
