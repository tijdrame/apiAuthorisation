package com.boa.api.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import com.boa.api.response.GenericResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class Utils {

    private final Logger log = LoggerFactory.getLogger(Utils.class);

    /*
     * private final Random RANDOM = new SecureRandom(); public String getRandomId
     * (){ return RandomStringUtils.randomAlphanumeric(16); }
     */

    public HttpURLConnection doConnexion(String endPoint, String params, String appType,
    String appRetour) throws IOException {
        OutputStream os = null;
        HttpURLConnection conn = null;
		try {
			log.info("end point wso2== [{}]", endPoint);
            URL url = new URL(endPoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", appType);
            if(StringUtils.isEmpty(appRetour))  conn.setRequestProperty("Accept", appRetour);
			
			//tracking.setRequestTr(jsonString);
            os = conn.getOutputStream();
            byte[] postDataBytes = params.getBytes();
            os.write(postDataBytes);
            os.flush();
        }catch(Exception e){
            log.error("Error in doConn endpoint[{}], params [{}] & trace [{}]", e);
            return conn;
        }
        os.close();
        return conn;
    }

    public ResponseEntity<String> doRestTemplate(String request, String endPoint) {
        HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
      GenericResponse gResponse = new GenericResponse();
      gResponse.setCode("200");
      HttpEntity<GenericResponse> entity = new HttpEntity<GenericResponse>(gResponse,headers);
      
        return new RestTemplate()
        .exchange(endPoint, HttpMethod.POST, entity, String.class);
    }
}