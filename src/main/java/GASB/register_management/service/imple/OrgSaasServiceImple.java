package GASB.register_management.service.imple;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.entity.Saas;
import GASB.register_management.repository.SaasRepository;
import GASB.register_management.service.OrgSaasService;
import GASB.register_management.entity.OrgSaas;
import GASB.register_management.entity.Workspace;
import GASB.register_management.repository.OrgSaasRepository;
import GASB.register_management.repository.WorkspaceRepository;

import GASB.register_management.util.validation.SlackTeamInfo;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrgSaasServiceImple implements OrgSaasService {

    @Autowired
    private OrgSaasRepository orgSaasRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private SaasRepository saasRepository;
    @Autowired
    private SlackTeamInfo slackTeamInfo;

    @Override
    public OrgSaasResponse getUrl(Integer saasId){
        Optional<Saas> saasOptional = saasRepository.findById(saasId);

        if(saasOptional.isPresent()){
            Saas saas = saasOptional.get();

            return new OrgSaasResponse("success", "http//gurm.com/" + saas.getSaas_name() + "/" + UUID.randomUUID().toString());
        }
        else {
            return new OrgSaasResponse("failure: Saas not found for id" + saasId, "");
        }
    }

    @Override
    public OrgSaasResponse registerOrgSaas(OrgSaasRequest orgSaasRequest) {
        OrgSaas orgSaas = new OrgSaas();
        Workspace workSpace = new Workspace();

        try {
            List<String> slackInfo;
            slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getToken());
            String teamName = slackInfo.get(0);
            workSpace.setWorkspace_name(teamName);
//            String teamId = slackInfo.get(1);
//            String teamUrl = slackInfo.get(2);
        } catch (IOException | InterruptedException e) {
            return new OrgSaasResponse("failure: " + e.getMessage(), null, null, null);
        }

//        < Req >
//        {
//            "org_id": int,                  // FE가 입력 // 세션에 있을듯?
//            "saas_id": int,                 // 등록할 때 받아서 POST
//            "space_id": string,             // space_id랑 뭐가 다르지?
//            "workspace_name": string,       // API로 얻어서 입력 // 서현이가? // valid때 내가 할수도? // client가 입력할수도?
//            "saas_admin_email": string,
//            "webhook_url": string,          // BE가 생성해서 넘겨준 url // 보여주기만 하고 다시 POST
//            "token": string                // client가 입력
//        }
//        < Resp >
//        {
//            "message": string,
//            "config_id": int,               // workspace_config 튜플의 id // 수정을 위해 반환받아야 함 // get pooling있어서 필요 없을지도?
//            "workspace_name": string,       // 입력한 그 이름
//            "register_date": timestamp      // YYYY/MM/DD
//        }

        // config
//      workSpace.setAlias(orgSaasRequest.getAlias());  // 용도가 애매해
        workSpace.setSaas_admin_email(orgSaasRequest.getSaas_admin_email());
        workSpace.setWebhook(orgSaasRequest.getWebhook_url());
        workSpace.setToken(orgSaasRequest.getToken());
        workSpace.setRegister_date(Timestamp.valueOf(LocalDateTime.now()));
        Workspace saveWorkSpace = workspaceRepository.save(workSpace);
        // OrgSaas
        orgSaas.setConfig(saveWorkSpace.getId());
        orgSaas.setOrg_id(orgSaasRequest.getOrg_id());;
        orgSaas.setSaas_id(orgSaasRequest.getSaas_id());
        orgSaas.setSpace_id(String.valueOf(orgSaasRequest.getSpace_id()));  // Setter가 id라 int로 인식한듯?
        OrgSaas saveOrgSaas = orgSaasRepository.save(orgSaas);

        return new OrgSaasResponse("Success", saveWorkSpace.getId(), saveWorkSpace.getWorkspace_name(), saveWorkSpace.getRegister_date());

    }

    @Override
    public OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest) {
//        Workspace workspace = new Workspace();
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(Long.valueOf(orgSaasRequest.getConfig_id()));
        Workspace workspace = optionalWorkspace.get();
//        < Req >
//        {
//            "config_id": Long,
//            "workspace_name": string,
//            "saas_admin_email": string,
//            "token": string
//          "webhook_url": string
//        }
//        < Resp >
//        {
//            "message": string,
//            "config_id": int,
//            "workspace_name": string,
//            "register_date": ts
//        }
        // Slack API 호출
        try {
            List<String> slackInfo;
            slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getToken());
            String teamName = slackInfo.get(0);
            workspace.setWorkspace_name(teamName);
        } catch (IOException | InterruptedException e) {
            return new OrgSaasResponse("failure: " + e.getMessage(), null, null, null);
        }

        if(optionalWorkspace.isPresent()) {

//            // 이렇게 안하면 입력 안한 속성이 null되버림
//            if (orgSaasRequest.getWorkspace_name() != null) {
//                workspace.setWorkspace_name(orgSaasRequest.getWorkspace_name());
//            }
            if (orgSaasRequest.getSaas_admin_email() != null) {
                workspace.setSaas_admin_email(orgSaasRequest.getSaas_admin_email());
            }
            if (orgSaasRequest.getToken() != null) {
                workspace.setToken(orgSaasRequest.getToken());
            }
            if (orgSaasRequest.getWebhook_url() != null) {
                workspace.setWebhook(orgSaasRequest.getWebhook_url());
            }
            workspace.setRegister_date(Timestamp.valueOf(LocalDateTime.now()));     //register_date긴한데 그냥 last_modified~로 이해하면 됨
            Workspace saveWorkspace = workspaceRepository.save(workspace);

            return new OrgSaasResponse("Success", saveWorkspace.getId(), saveWorkspace.getWorkspace_name(), saveWorkspace.getRegister_date());
        }
        else {
            return new OrgSaasResponse("Failure: Not found by ID", orgSaasRequest.getConfig_id(), null, null);
        }
    }

    @Override
    public OrgSaasResponse deleteOrgSaas(OrgSaasRequest orgSaasRequest) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(Long.valueOf(orgSaasRequest.getConfig_id()));


        if(optionalWorkspace.isPresent()) {
            Workspace workspace = optionalWorkspace.get();

            List<OrgSaas> orgSaasList = orgSaasRepository.findByConfig(Long.valueOf(orgSaasRequest.getConfig_id()));
            orgSaasRepository.deleteAll(orgSaasList);
            workspaceRepository.delete(workspace);
            return new OrgSaasResponse("Success", workspace.getId());
        }
        else {
            return new OrgSaasResponse("failure", orgSaasRequest.getConfig_id());
        }
    }

    @Override
    public List<OrgSaasResponse> getOrgSaasList(Integer orgId) {
        List<Object[]> results = orgSaasRepository.findByOrgId(orgId);

        return results.stream().map(result -> {
            OrgSaas orgSaas = (OrgSaas) result[0];
            Workspace workspace = (Workspace) result[1];

            return new OrgSaasResponse(
                    "Success",
                    orgSaas.getSaas_id(),
                    orgSaas.getConfig(),
                    orgSaas.getStatus(),

                    workspace.getWorkspace_name(),
                    workspace.getToken(),
                    workspace.getWebhook(),
                    workspace.getSaas_admin_email(),
                    workspace.getRegister_date()
            );
        }).collect(Collectors.toList());
    }


}
