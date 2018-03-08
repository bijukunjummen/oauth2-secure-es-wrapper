package org.bk.samples;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ElasticSentryController {

    private String elasticSearchURL;

    @Value("${elasticsearch.url}")
    void setElasticSearchURL(String elasticSearchURL) {
        this.elasticSearchURL = elasticSearchURL;
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("#oauth2.hasScope('resource.read')")
    public ResponseEntity<?> elasticSearchGet(@RequestBody(required=false) String body, HttpServletRequest request) {
        final String url = new StringBuilder(this.elasticSearchURL)
            .append(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).toString();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(request.getContentType()));
        try {
            return new RestTemplate().getForEntity(url, String.class);
        } catch(HttpClientErrorException ex) {
            return new ResponseEntity<String>(ex.getResponseBodyAsString(), ex.getStatusCode());
        }
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @PreAuthorize("#oauth2.hasScope('resource.write')")
    public ResponseEntity<?> updateEndpoints(@RequestBody(required=false) String body, HttpServletRequest request) {
        final String url = new StringBuilder(this.elasticSearchURL)
                .append(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).toString();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(request.getContentType()));
        try {
            if(RequestMethod.POST.toString().equals(request.getMethod())) {
                return new RestTemplate().postForEntity(url, new HttpEntity<String>(body, headers), String.class);
            } else if(RequestMethod.GET.toString().equals(request.getMethod())) {
                return new RestTemplate().getForEntity(url, String.class);
            } else if(RequestMethod.DELETE.toString().equals(request.getMethod())) {
                new RestTemplate().delete(url);
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else if(RequestMethod.PUT.toString().equals(request.getMethod())) {
                new RestTemplate().put(url, new HttpEntity<String>(body, headers));
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else {
                return new ResponseEntity<Void>(HttpStatus.METHOD_NOT_ALLOWED);
            }
        } catch(HttpClientErrorException ex) {
            return new ResponseEntity<String>(ex.getResponseBodyAsString(), ex.getStatusCode());
        }
    }
}