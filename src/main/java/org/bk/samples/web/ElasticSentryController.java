package org.bk.samples.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ElasticSentryController {

    private String elasticSearchURL;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${elasticsearch.url}")
    void setElasticSearchURL(String elasticSearchURL) {
        this.elasticSearchURL = elasticSearchURL;
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("#oauth2.hasScope('resource.read')")
    public ResponseEntity<?> elasticSearchGet(@RequestBody(required = false) String body, HttpServletRequest request, Authentication principal) {
//        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)principal;
//        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
//        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)oAuth2Authentication.getDetails();
//        System.out.println(oAuth2AuthenticationDetails.getTokenValue());
        final String url = new StringBuilder(this.elasticSearchURL)
                .append(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).toString();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(request.getContentType()));
        try {
            return this.restTemplate.getForEntity(url, String.class);
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
        }
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @PreAuthorize("#oauth2.hasScope('resource.write')")
    public ResponseEntity<?> updateEndpoints(@RequestBody(required = false) String body, HttpServletRequest request) {
        final String url = new StringBuilder(this.elasticSearchURL)
                .append(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).toString();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(request.getContentType()));
        try {
            if (RequestMethod.POST.toString().equals(request.getMethod())) {
                return this.restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
            } else if (RequestMethod.GET.toString().equals(request.getMethod())) {
                return new RestTemplate().getForEntity(url, String.class);
            } else if (RequestMethod.DELETE.toString().equals(request.getMethod())) {
                this.restTemplate.delete(url);
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else if (RequestMethod.PUT.toString().equals(request.getMethod())) {
                this.restTemplate.put(url, new HttpEntity<String>(body, headers));
                return ResponseEntity.ok("");
            } else {
                return new ResponseEntity<Void>(HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch (HttpClientErrorException ex) {
            return new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
        }
    }
}