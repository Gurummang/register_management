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

import GASB.register_management.util.GoogleUtil;
import GASB.register_management.util.api.StartScan;
import GASB.register_management.util.validation.SlackTeamInfo;
import com.google.api.client.auth.oauth2.Credential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrgSaasServiceImple implements OrgSaasService {

    private final OrgSaasRepository orgSaasRepository;
    private final WorkspaceRepository workspaceRepository;
    private final SaasRepository saasRepository;
    private final SlackTeamInfo slackTeamInfo;
    private final StartScan startScan;
    private final GoogleUtil googleUtil;

    @Autowired
    public OrgSaasServiceImple(OrgSaasRepository orgSaasRepository, WorkspaceRepository workspaceRepository, SaasRepository saasRepository, SlackTeamInfo slackTeamInfo, StartScan startScan, GoogleUtil googleUtil) {
        this.orgSaasRepository = orgSaasRepository;
        this.workspaceRepository = workspaceRepository;
        this.saasRepository = saasRepository;
        this.slackTeamInfo = slackTeamInfo;
        this.startScan = startScan;
        this.googleUtil = googleUtil;
    }

    @Override
    public OrgSaasResponse slackValid(OrgSaasRequest orgSaasRequest) {
        String token = orgSaasRequest.getApiToken();
        try {
            slackTeamInfo.getTeamInfo(token);

            return new OrgSaasResponse(200, null, true);
        } catch (IOException | InterruptedException e) {
            return new OrgSaasResponse(199, e.getMessage(), false);
        }
    }

    @Override
    public OrgSaasResponse getUrl(Integer saasId) {
        Optional<Saas> saasOptional = saasRepository.findById(saasId);

        if(saasOptional.isPresent()) {
            Saas saas = saasOptional.get();

            System.out.println(saas.getSaasName());
            return new OrgSaasResponse( 200, null,
                    "https://back.grummang.com/webhook/"+saas.getSaasName()+ "/" + UUID.randomUUID());
        }else {
            return new OrgSaasResponse( 199, "Not found for ID", "");
        }
    }

    @Override
    public OrgSaasResponse registerOrgSaas(OrgSaasRequest orgSaasRequest, Credential credential_value) {
        System.out.println("2. 등록 Serive");
        OrgSaas orgSaas = new OrgSaas();
        Workspace workspace = new Workspace();

        if(orgSaasRequest.getSaasId() == 6) {
            System.out.println("3. saasId == 6, 구글 드라이브 연동 시도");
            try {

                System.out.println("4. googleUtil.getCredentials() 호출");
                Credential credential = credential_value;
//                Credential credential1 = googleUtil.getCredentials(credential);
                System.out.println("8. credential 반환: " + credential);
                String accessToken = credential.getAccessToken();
                System.out.println("9. accessToken 반환 " + accessToken);
                System.out.println("9-1 refreshToken "+ credential.getRefreshToken());
                try {
                    System.out.println("10. getDriveService 호출");
                    Drive drive = googleUtil.getDriveService(credential);
                    List<String[]> drives = googleUtil.getAllSharedDriveIdsAndNames(drive);
                    Workspace regiWorkspace = new Workspace();
                    for (String[] driveInfo : drives) {
                        OrgSaas orgSaas2 = new OrgSaas();
                        Workspace workspace2 = new Workspace();

                        orgSaas2.setOrgId(orgSaasRequest.getOrgId());
                        orgSaas2.setSaasId(orgSaasRequest.getSaasId());
                        orgSaas2.setSpaceId(driveInfo[0]);
                        OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas2);

                        workspace2.setId(regiOrgSaas.getId());
                        workspace2.setSpaceName(driveInfo[1]);
                        workspace2.setAlias(orgSaasRequest.getAlias());
                        workspace2.setAdminEmail(orgSaasRequest.getAdminEmail());
                        workspace2.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                        workspace2.setApiToken(accessToken);
                        workspace2.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                        regiWorkspace = workspaceRepository.save(workspace2);
                    }

                    return new OrgSaasResponse( 200, null, regiWorkspace.getId(), regiWorkspace.getRegisterDate());
                } catch (Exception e) {
                    return new OrgSaasResponse(199, "Can Not Returned Google Drives", null, null);
                }
            } catch (Exception e) {
                return new OrgSaasResponse(199, "Can Not Returned Google Credentials", null, null);
            }
        } else {
            try {
                List<String> slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getApiToken());

                // org_saas
                orgSaas.setOrgId(orgSaasRequest.getOrgId());    // workspace_config.id
                orgSaas.setSaasId(orgSaasRequest.getSaasId());
                orgSaas.setSpaceId(slackInfo.get(1));
                OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);

                // workspace_config
                workspace.setId(regiOrgSaas.getId());
                workspace.setSpaceName(slackInfo.get(0));
                workspace.setAlias(orgSaasRequest.getAlias());
                workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                workspace.setApiToken(orgSaasRequest.getApiToken());
                workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                Workspace registeredWorkspace = workspaceRepository.save(workspace);

                //saasId -> saasName
                String saasName = saasRepository.findById(orgSaasRequest.getSaasId()).get().getSaasName();

                try{
                    startScan.postToScan(registeredWorkspace.getId(), saasName);

                    return new OrgSaasResponse( 200, null, registeredWorkspace.getId(), registeredWorkspace.getRegisterDate());
                } catch (Exception e) {
                    return new OrgSaasResponse(198, e.getMessage(), null, null);
                }

            } catch (IOException | InterruptedException e) {
                return new OrgSaasResponse( 199, e.getMessage(),null, null);
            }
        }
    }

    @Override
    public OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest) {
        Optional<OrgSaas> optionalOrgSaas = orgSaasRepository.findById(orgSaasRequest.getId());
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(orgSaasRequest.getId());

        if(optionalOrgSaas.isPresent() && optionalWorkspace.isPresent()) {
            OrgSaas orgSaas = optionalOrgSaas.get();
            Workspace workspace = optionalWorkspace.get();

            try{
                List<String> slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getApiToken());

                // org_saas
                orgSaas.setSpaceId(slackInfo.get(1));
                OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);

                // workspace_config
                workspace.setSpaceName(slackInfo.get(0));
                workspace.setAlias(orgSaasRequest.getAlias());
                workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                workspace.setApiToken(orgSaasRequest.getApiToken());
                workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                Workspace registeredWorkspace = workspaceRepository.save(workspace);

                //saasId -> saasName
                Integer saasId = orgSaasRepository.findById(orgSaasRequest.getId()).get().getSaasId();
                String saasName = saasRepository.findById(saasId).get().getSaasName();

                try{
                    startScan.postToScan(registeredWorkspace.getId(), saasName);

                    return new OrgSaasResponse( 200, null, registeredWorkspace.getId(), registeredWorkspace.getRegisterDate());
                } catch (Exception e) {
                    return new OrgSaasResponse(198, e.getMessage(), null, null);
                }

            } catch (IOException | InterruptedException e) {
                return new OrgSaasResponse( 199, e.getMessage(),null, null);
            }
        } else {
            return new OrgSaasResponse( 199, "Not found for ID", "");
        }
    }

    @Override
    public OrgSaasResponse deleteOrgSaas(OrgSaasRequest orgSaasRequest) {
        Optional<OrgSaas> optionalOrgSaas = orgSaasRepository.findById(orgSaasRequest.getId());

        if (optionalOrgSaas.isPresent()) {
            OrgSaas orgSaas = optionalOrgSaas.get();

            // orgSaas의 튜플을 삭제하면
            // 자식 튜플(config, monitored_users 등)들 모두 CASCADE로 삭제
            orgSaasRepository.delete(orgSaas);

            return new OrgSaasResponse( 200, null, null,null);
        } else {
            return new OrgSaasResponse( 199, "Not found for ID", null,null);
        }
    }

    @Override
    public List<OrgSaasResponse> getOrgSaasList(Integer orgId) {
        // 1. orgId로 org_saas 테이블에서 튜플 조회
        List<OrgSaas> orgSaasList = orgSaasRepository.findByOrgId(orgId);

        // 2. 조회된 orgSaas 데이터에서 config 값을 추출
        List<Integer> configIds = orgSaasList.stream()
                .map(OrgSaas::getId)
                .distinct()  // 중복 제거
                .collect(Collectors.toList());

        // 3. config 값을 사용하여 workspace_config 테이블에서 데이터 조회
        List<Workspace> workspaceList = workspaceRepository.findByIdIn(configIds);

        // 4. configId를 기준으로 Workspace 객체를 맵으로 변환
        Map<Integer, Workspace> workspaceMap = workspaceList.stream()
                .collect(Collectors.toMap(Workspace::getId, workspace -> workspace));

        // 5. 결과 리스트 생성
        return orgSaasList.stream().map(orgSaas -> {
            // Lookup workspace by configId
            Workspace workspace = workspaceMap.get(orgSaas.getId());

            Optional<Saas> saasOptional = saasRepository.findById(orgSaas.getSaasId());
            String saasName = saasOptional.map(Saas::getSaasName).orElse("Unknown");

            return new OrgSaasResponse(
                    workspace != null ? workspace.getId() : null,
                    saasName,
                    workspace != null ? workspace.getAlias() : null,
                    orgSaas.getStatus(),
                    workspace != null ? workspace.getAdminEmail() : null,
                    workspace != null ? workspace.getApiToken() : null,
                    workspace != null ? workspace.getWebhookUrl() : null,
                    workspace != null ? workspace.getRegisterDate() : null
            );
        }).collect(Collectors.toList());
    }

}
