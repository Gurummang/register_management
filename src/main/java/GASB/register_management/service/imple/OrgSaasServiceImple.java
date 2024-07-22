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
    public OrgSaasResponse getUrl(Integer saasId) {
        Optional<Saas> saasOptional = saasRepository.findById(saasId);

        if(saasOptional.isPresent()) {
            Saas saas = saasOptional.get();

            return new OrgSaasResponse(true, 200, null,
                    "https://gurm.com/"+saas.getSaas_name() + "-" + UUID.randomUUID().toString());
        }else {
            return new OrgSaasResponse(false, 199, "Not found for ID", null);
        }
    }

    @Override
    public OrgSaasResponse registerOrgSaas(OrgSaasRequest orgSaasRequest) {
        OrgSaas orgSaas = new OrgSaas();
        Workspace workspace = new Workspace();

        try {
            List<String> slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getApiToken());
            // token validation
            String spaceName = slackInfo.get(0);
            String spaceId = slackInfo.get(1);
            workspace.setSpaceName(spaceName);
            orgSaas.setSpaceId(spaceId);
            workspace.setValidation("Valid");
            // workspace_config
            workspace.setAlias(orgSaasRequest.getAlias());
            workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
            workspace.setApiToken(orgSaasRequest.getApiToken());
            workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
            workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
            Workspace registeredWorkspace = workspaceRepository.save(workspace);
            // org_saas
            orgSaas.setOrgId(orgSaasRequest.getOrgId());
            orgSaas.setSaasId(orgSaasRequest.getSaasId());
            orgSaas.setConfig(registeredWorkspace.getConfigId());
            orgSaasRepository.save(orgSaas);

            return new OrgSaasResponse(true, 200, null, registeredWorkspace.getConfigId(), registeredWorkspace.getRegisterDate());
        } catch (IOException | InterruptedException e) {
            workspace.setSpaceName("NULL");
            orgSaas.setSpaceId("NULL");
            workspace.setValidation("Invalid");
            //workspace_config
            workspace.setAlias(orgSaasRequest.getAlias());
            workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
            workspace.setApiToken(orgSaasRequest.getApiToken());
            workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
            workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
            Workspace registeredWorkspace = workspaceRepository.save(workspace);
            // org_saas
            orgSaas.setOrgId(orgSaasRequest.getOrgId());
            orgSaas.setSaasId(orgSaasRequest.getSaasId());
            orgSaas.setConfig(registeredWorkspace.getConfigId());
            orgSaasRepository.save(orgSaas);

            return new OrgSaasResponse(true, 201, "API token Invalid", registeredWorkspace.getConfigId(), registeredWorkspace.getRegisterDate());
        }
    }

    @Override
    public OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(Long.valueOf(orgSaasRequest.getConfigId()));
        Optional<OrgSaas> optionalOrgSaas = orgSaasRepository.findById(orgSaasRequest.getConfigId());

        if (optionalWorkspace.isPresent() && optionalOrgSaas.isPresent()) {
            Workspace workspace = optionalWorkspace.get();
            OrgSaas orgSaas = optionalOrgSaas.get();
            try {
                List<String> slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getApiToken());
                // token validation
                String spaceName = slackInfo.get(0);
                String spaceId = slackInfo.get(1);
                workspace.setSpaceName(spaceName);
                orgSaas.setSpaceId(spaceId);
                workspace.setValidation("Valid");
                // workspace_config
                workspace.setAlias(orgSaasRequest.getAlias());
                workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                workspace.setApiToken(orgSaasRequest.getApiToken());
                workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                Workspace registeredWorkspace = workspaceRepository.save(workspace);
                // org_saas
                orgSaas.setConfig(registeredWorkspace.getConfigId());
                orgSaasRepository.save(orgSaas);

                return new OrgSaasResponse(true, 200, null, registeredWorkspace.getConfigId(), registeredWorkspace.getRegisterDate());
            } catch (IOException | InterruptedException e) {
                workspace.setSpaceName("NULL");
                orgSaas.setSpaceId("NULL");
                workspace.setValidation("Invalid");
                // workspace_config
                workspace.setAlias(orgSaasRequest.getAlias());
                workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                workspace.setApiToken(orgSaasRequest.getApiToken());
                workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                Workspace registeredWorkspace = workspaceRepository.save(workspace);
                // org_saas
                orgSaas.setConfig(registeredWorkspace.getConfigId());
                orgSaasRepository.save(orgSaas);

                return new OrgSaasResponse(true, 201, "API token Invalid", registeredWorkspace.getConfigId(), registeredWorkspace.getRegisterDate());
            }
        } else {
            // 두 Optional 중 하나라도 비어있을 경우 처리
            String errorMessage = "Not found for ID";
            if (optionalWorkspace.isEmpty()) {
                errorMessage += " (Workspace)";
            }
            if (optionalOrgSaas.isEmpty()) {
                errorMessage += " (OrgSaas)";
            }
            return new OrgSaasResponse(true, 199, errorMessage, null);
        }
    }


    @Override
    public OrgSaasResponse deleteOrgSaas(OrgSaasRequest orgSaasRequest) {
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(Long.valueOf(orgSaasRequest.getConfigId()));

        if(optionalWorkspace.isPresent()) {
            Workspace workspace = optionalWorkspace.get();

            List<OrgSaas> orgSaasList = orgSaasRepository.findByConfig(orgSaasRequest.getConfigId());
            orgSaasRepository.deleteAll(orgSaasList);
            workspaceRepository.delete(workspace);

            return new OrgSaasResponse(true, 200, null, null);
        } else {
            return new OrgSaasResponse(true, 199, "Not found for ID", null);
        }
    }

    @Override
    public List<OrgSaasResponse> getOrgSaasList(Integer orgId) {
        List<Object[]> results = orgSaasRepository.findByOrgId(orgId);

        return results.stream().map(result -> {
            OrgSaas orgSaas = (OrgSaas) result[0];
            Workspace workspace = (Workspace) result[1];

            Optional<Saas> saasOptional = saasRepository.findById(orgSaas.getSaasId());
            String saasName = saasOptional.get().getSaas_name();

            return new OrgSaasResponse(
                    true,
                    200,
                    null,
                    workspace.getConfigId(),
                    saasName,
                    workspace.getAlias(),
                    orgSaas.getStatus(),
                    workspace.getAdminEmail(),
                    workspace.getApiToken(),
                    workspace.getValidation(),
                    workspace.getWebhookUrl(),
                    workspace.getRegisterDate()
            );
        }).collect(Collectors.toList());
    }
}
