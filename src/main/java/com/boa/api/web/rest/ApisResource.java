package com.boa.api.web.rest;

import com.boa.api.request.AnnulationRequest;
import com.boa.api.request.AuthorisationRequest;
import com.boa.api.request.GetCommissionRequest;
import com.boa.api.response.AnnulationResponse;
import com.boa.api.response.AuthorisationResponse;
import com.boa.api.response.GetCommissionResponse;
import com.boa.api.service.ApiService;
import com.boa.api.utils.ICodeDescResponse;
import java.net.URISyntaxException;
import java.time.Instant;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ApisResource controller
 */
@RestController
@RequestMapping("/api")
public class ApisResource {

    private final Logger log = LoggerFactory.getLogger(ApisResource.class);

    private final ApiService apiService;

    public ApisResource(ApiService apiService) {
        this.apiService = apiService;
    }

    @PostMapping("/authorisation")
    public ResponseEntity<AuthorisationResponse> authorisation(@RequestBody AuthorisationRequest authoRequest, HttpServletRequest request) {
        log.info("Enter in authorisation [{}]", authoRequest);
        AuthorisationResponse response = new AuthorisationResponse();
        // doControl
        if (
            StringUtils.isEmpty(authoRequest.getReferenceTransfert()) ||
            StringUtils.isEmpty(authoRequest.getCompteEmetteur()) ||
            StringUtils.isEmpty(authoRequest.getMontant().toString())
        ) {
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(ICodeDescResponse.PARAM_DESCRIPTION);
            return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
        }

        response = apiService.authorisation(authoRequest, request);
        log.info("Response in authorisation [{}]", response);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    @PostMapping("/annulation")
    public ResponseEntity<AnnulationResponse> annulation(@RequestBody AnnulationRequest annuRequest, HttpServletRequest request) {
        log.info("Enter in annulation [{}]", annuRequest);
        AnnulationResponse response = new AnnulationResponse();
        // doControl
        if (StringUtils.isEmpty(annuRequest.getReferenceTransfert()) || StringUtils.isEmpty(annuRequest.getCountry())) {
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(ICodeDescResponse.PARAM_DESCRIPTION);
            return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
        }

        response = apiService.annulation(annuRequest, request);
        log.info("Response in annulation [{}]", response);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    @PostMapping("/getCommission")
    public ResponseEntity<GetCommissionResponse> getCommission(@RequestBody GetCommissionRequest cRequest, HttpServletRequest request)
        throws URISyntaxException {
        log.info("REST request to getCommission :======= [{}]", cRequest);
        GetCommissionResponse response = new GetCommissionResponse();
        if (
            controleParam(cRequest.getCodeOperation()) ||
            controleParam(cRequest.getLangue()) ||
            controleParam(cRequest.getMontant()) ||
            controleParam(cRequest.getCompte()) ||
            controleParam(cRequest.getCountry())
        ) {
            log.info("param ko======");
            response.setCode(ICodeDescResponse.PARAM_ABSENT_CODE);
            response.setDateResponse(Instant.now());
            response.setDescription(ICodeDescResponse.PARAM_DESCRIPTION);
            return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
        }

        response = apiService.getCommission(cRequest, request);
        return ResponseEntity.ok().header("Authorization", request.getHeader("Authorization")).body(response);
    }

    public Boolean controleParam(Object param) {
        Boolean flag = false;
        if (StringUtils.isEmpty(param)) flag = true;
        return flag;
    }
}
