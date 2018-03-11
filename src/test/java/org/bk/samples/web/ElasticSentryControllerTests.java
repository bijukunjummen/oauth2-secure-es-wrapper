package org.bk.samples.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ElasticSentryController.class)
public class ElasticSentryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer server;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${elasticsearch.url}")
    private String elasticSearchUrl;

    @Before
    public void setup() {
        this.server = MockRestServiceServer.createServer(restTemplate);
    }
    
    @Test
    public void testGetPassthrough() throws Exception {
        this.server.expect(requestTo(elasticSearchUrl + "/_search"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        
        this.mockMvc.perform(
                get("/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("test"))
        )
                .andExpect(status().isOk());
    }
}
