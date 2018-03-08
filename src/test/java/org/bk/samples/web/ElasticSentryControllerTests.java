package org.bk.samples.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ElasticSentryController.class)
public class ElasticSentryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetPassthrough() throws Exception {
        this.mockMvc.perform(
                get("/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("test"))
        )
                .andExpect(status().isOk());
    }
}
