package com.boa.api.service;

import com.boa.api.domain.ParamEndPoint;
import com.boa.api.domain.ParamGeneral;
import com.boa.api.domain.Tracking;
import com.boa.api.request.AnnulationRequest;
import com.boa.api.request.AuthorisationRequest;
import com.boa.api.request.GetCommissionRequest;
import com.boa.api.response.AnnulationResponse;
import com.boa.api.response.AuthorisationResponse;
import com.boa.api.response.GetCommissionResponse;
import com.boa.api.utils.ICodeDescResponse;
import com.boa.api.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApiService {

    private final Logger log = LoggerFactory.getLogger(ApiService.class);

    private final TrackingService trackingService;
    private final UserService userService;
    private final Utils utils;
    private final ParamEndPointService endPointService;

    private final ParamGeneralService paramGeneralService;

    public ApiService(
        TrackingService trackingService,
        UserService userService,
        Utils utils,
        ParamEndPointService endPointService,
        ParamGeneralService paramGeneralService
    ) {
        this.trackingService = trackingService;
        this.userService = userService;
        this.utils = utils;
        this.endPointService = endPointService;
        this.paramGeneralService = paramGeneralService;
    }

    public AuthorisationResponse authorisation(AuthorisationRequest authoRequest, HttpServletRequest request) {
        log.info("Enter in authorisation=== [{}]", authoRequest);
        AuthorisationResponse genericResp = new AuthorisationResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        ParamEndPoint endPoint = endPointService.findByCodeParam("authorisation");
        if (endPoint == null) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(ICodeDescResponse.SERVICE_ABSENT_DESC);
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "authorisation",
                    genericResp.toString(),
                    authoRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        Optional<ParamGeneral> optionalPM = paramGeneralService.findByCodeAndPays(ICodeDescResponse.COMPTE_DAP, authoRequest.getCountry());
        if (!optionalPM.isPresent()) {
            genericResp.setCode(ICodeDescResponse.FILIALE_ABSENT_CODE);
            genericResp.setDescription(ICodeDescResponse.COMPTE_DAP_ABSENT);
            genericResp.setDateResponse(Instant.now());
            return genericResp;
        }

        GetCommissionRequest commissionRequest = new GetCommissionRequest();
        commissionRequest
            .codeOperation(optionalPM.get().getVarString2())
            .compte(authoRequest.getCompteEmetteur())
            .country(authoRequest.getCountry())
            .devise("")
            .montant(Double.valueOf(authoRequest.getMontant()))
            .langue("fr");
        GetCommissionResponse commissionResponse = getCommission(commissionRequest, request);
        if (commissionResponse == null || commissionResponse.getCode().equals("200")) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(
                commissionResponse != null ? commissionResponse.getRMessage() : ICodeDescResponse.FRAIS_NON_REMONTEE
            );
            genericResp.setDateResponse(Instant.now());
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("montant", authoRequest.getMontant())
                .put("operateur", authoRequest.getOperation())
                .put("referencetransfert", authoRequest.getReferenceTransfert())
                .put("numerotransaction", authoRequest.getNumeroTransaction())
                .put("compte_emetteur", authoRequest.getCompteEmetteur())
                .put("disponible", authoRequest.getDisponible())
                .put("valdisponible", authoRequest.getValDisponible())
                .put("country", authoRequest.getCountry())
                //.put("mntfrais", authoRequest.getMntFrais())
                .put("mntfrais", commissionResponse.getMontantCommission())
                .put("libelle", authoRequest.getLibelle())
                .put("compte_crediteur", authoRequest.getCompteCrediteur())
                .put("codAuto", authoRequest.getCodAuto())
                .toString();

            log.info("request confirmation [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.getEndPoints(), jsonStr, "application/json", "");
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code envoi [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("resp envoi ===== [{}]", result);
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(result, Map.class);
                obj = new JSONObject(result);
                genericResp.setData(map);
                if (obj.toString().contains("reussi")) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(ICodeDescResponse.SUCCES_DESCRIPTION);
                    genericResp.setDateResponse(Instant.now());
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            result,
                            authoRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(ICodeDescResponse.ECHEC_DESCRIPTION);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            result,
                            authoRequest.toString(),
                            genericResp.getResponseReference()
                        );
                }
            } else {
                // conn =null
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("resp envoi error ===== [{}]", result);
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(result, Map.class);
                genericResp.setData(map);
                genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResp.setDateResponse(Instant.now());
                genericResp.setDescription(ICodeDescResponse.ECHEC_DESCRIPTION);
                tracking =
                    createTracking(
                        tracking,
                        ICodeDescResponse.ECHEC_CODE,
                        request.getRequestURI(),
                        genericResp.toString(),
                        authoRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in authorisation [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(ICodeDescResponse.ECHEC_DESCRIPTION + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    authoRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public AnnulationResponse annulation(AnnulationRequest annuRequest, HttpServletRequest request) {
        log.info("Enter in annulation [{}]", annuRequest);
        AnnulationResponse genericResp = new AnnulationResponse();
        Tracking tracking = new Tracking();
        tracking.setDateRequest(Instant.now());

        ParamEndPoint endPoint = endPointService.findByCodeParam("annulation");
        if (endPoint == null) {
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDescription(ICodeDescResponse.SERVICE_ABSENT_DESC);
            genericResp.setDateResponse(Instant.now());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    "authorisation",
                    genericResp.toString(),
                    annuRequest.toString(),
                    genericResp.getResponseReference()
                );
            trackingService.save(tracking);
            return genericResp;
        }
        try {
            String jsonStr = new JSONObject()
                .put("referencetransfert", annuRequest.getReferenceTransfert())
                .put("codeoperateur", annuRequest.getOperation())
                .put("country", annuRequest.getCountry())
                .toString();

            log.info("request confirmation [{}]", jsonStr);
            HttpURLConnection conn = utils.doConnexion(endPoint.getEndPoints(), jsonStr, "application/json", "");
            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            String result = "";
            log.info("resp code envoi [{}]", conn.getResponseCode());
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("resp envoi ===== [{}]", result);
                obj = new JSONObject(result);
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(result, Map.class);
                genericResp.setData(map);
                if (obj.toString().contains("reussi")) {
                    genericResp.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResp.setDescription(ICodeDescResponse.SUCCES_DESCRIPTION);
                    genericResp.setDateResponse(Instant.now());
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            request.getRequestURI(),
                            result,
                            annuRequest.toString(),
                            genericResp.getResponseReference()
                        );
                } else {
                    genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResp.setDateResponse(Instant.now());
                    genericResp.setDescription(ICodeDescResponse.ECHEC_DESCRIPTION);
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            request.getRequestURI(),
                            result,
                            annuRequest.toString(),
                            genericResp.getResponseReference()
                        );
                }
            } else {
                // conn =null
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("resp envoi error ===== [{}]", result);
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(result, Map.class);
                genericResp.setData(map);
                genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResp.setDateResponse(Instant.now());
                genericResp.setDescription(ICodeDescResponse.ECHEC_DESCRIPTION);
                tracking =
                    createTracking(
                        tracking,
                        ICodeDescResponse.ECHEC_CODE,
                        request.getRequestURI(),
                        genericResp.toString(),
                        annuRequest.toString(),
                        genericResp.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("Exception in annulation [{}]", e);
            genericResp.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResp.setDateResponse(Instant.now());
            genericResp.setDescription(ICodeDescResponse.ECHEC_DESCRIPTION + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    request.getRequestURI(),
                    e.getMessage(),
                    annuRequest.toString(),
                    genericResp.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResp;
    }

    public GetCommissionResponse getCommission(GetCommissionRequest commissionRequest, HttpServletRequest request) {
        log.info("in get Commision [{}]", commissionRequest);
        GetCommissionResponse genericResponse = new GetCommissionResponse();
        Tracking tracking = new Tracking();
        String autho = request.getHeader("Authorization");
        String[] tab = autho.split("Bearer");
        ParamEndPoint filiale = endPointService.findByCodeParam("getCommission");
        String result = "";
        tracking.setDateRequest(Instant.now());
        try {
            if (filiale == null) {
                genericResponse.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResponse.setDescription(ICodeDescResponse.SERVICE_ABSENT_DESC);
                genericResponse.setDateResponse(Instant.now());
                tracking =
                    createTracking(
                        tracking,
                        ICodeDescResponse.ECHEC_CODE,
                        "getCommission",
                        genericResponse.toString(),
                        commissionRequest.toString(),
                        genericResponse.getResponseReference()
                    );
                trackingService.save(tracking);
                return genericResponse;
            }
            String jsonString = new JSONObject()
                .put("montant", commissionRequest.getMontant())
                .put("devise", commissionRequest.getDevise())
                .put("codeoper", commissionRequest.getCodeOperation())
                .put("compte", commissionRequest.getCompte())
                .put("country", commissionRequest.getCountry())
                .toString();

            BufferedReader br = null;
            JSONObject obj = new JSONObject();
            // String result = "";
            HttpURLConnection conn = utils.doConnexion(filiale.getEndPoints(), jsonString, "application/json", null);
            if (conn != null && conn.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String ligne = br.readLine();
                while (ligne != null) {
                    result += ligne;
                    ligne = br.readLine();
                }
                log.info("getCommission result ===== [{}]", result);
                obj = new JSONObject(result);
                obj = obj.getJSONObject("data");
                if (
                    obj.toString() != null &&
                    !obj.isNull("rcommission") &&
                    obj.toString().contains("rcode") &&
                    obj.getJSONObject("rcommission").getString("rcode").equals("00")
                ) {
                    genericResponse.setCode(ICodeDescResponse.SUCCES_CODE);
                    genericResponse.setDateResponse(Instant.now());
                    genericResponse.setDescription(ICodeDescResponse.SUCCES_DESCRIPTION);
                    genericResponse.setMontantCommission(obj.getJSONObject("rcommission").getDouble("commission"));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.SUCCES_CODE,
                            filiale.getEndPoints(),
                            result,
                            commissionRequest.toString(),
                            genericResponse.getResponseReference()
                        );
                } else if (
                    obj.toString() != null &&
                    !obj.isNull("rcommission") &&
                    obj.toString().contains("rcode") &&
                    !obj.getJSONObject("rcommission").getString("rcode").equals("00")
                ) {
                    genericResponse.setCode(ICodeDescResponse.ECHEC_CODE);
                    genericResponse.setDateResponse(Instant.now());
                    genericResponse.setDescription(ICodeDescResponse.ECHEC_DESCRIPTION);
                    genericResponse.setRCode(obj.getJSONObject("rcommission").getString("rcode"));
                    genericResponse.setRMessage(obj.getJSONObject("rcommission").getString("rmessage"));
                    tracking =
                        createTracking(
                            tracking,
                            ICodeDescResponse.ECHEC_CODE,
                            filiale.getEndPoints(),
                            result,
                            commissionRequest.toString(),
                            genericResponse.getResponseReference()
                        );
                }
            } else {
                genericResponse.setCode(ICodeDescResponse.ECHEC_CODE);
                genericResponse.setDateResponse(Instant.now());
                genericResponse.setDescription(ICodeDescResponse.ECHEC_DESCRIPTION);
                tracking =
                    createTracking(
                        tracking,
                        ICodeDescResponse.ECHEC_CODE,
                        filiale.getEndPoints(),
                        result,
                        commissionRequest.toString(),
                        genericResponse.getResponseReference()
                    );
            }
        } catch (Exception e) {
            log.error("exception in getCommission [{}]", e);
            genericResponse.setCode(ICodeDescResponse.ECHEC_CODE);
            genericResponse.setDateResponse(Instant.now());
            genericResponse.setDescription(ICodeDescResponse.FILIALE_ABSENT_DESC + " Message=" + e.getMessage());
            tracking =
                createTracking(
                    tracking,
                    ICodeDescResponse.ECHEC_CODE,
                    filiale.getEndPoints(),
                    e.getMessage(),
                    commissionRequest.toString(),
                    genericResponse.getResponseReference()
                );
        }
        trackingService.save(tracking);
        return genericResponse;
    }

    public Tracking createTracking(Tracking tracking, String code, String endPoint, String result, String req, String reqId) {
        // Tracking tracking = new Tracking();
        tracking.setCodeResponse(code);
        tracking.setDateResponse(Instant.now());
        tracking.setEndPoint(endPoint);
        tracking.setLoginActeur(userService.getUserWithAuthorities().get().getLogin());
        tracking.setResponseTr(result);
        tracking.setRequestTr(req);
        tracking.setRequestId(reqId);
        return tracking;
    }
}
