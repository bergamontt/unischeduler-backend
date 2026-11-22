package ua.edu.ukma.unischeduler.feature.scheduleimport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "unischeduler.import.enabled=false",
        "UNISCHEDULER_IMPORT_TOKEN=test-only-import-token"
})
@AutoConfigureMockMvc
class ScheduleImportDisabledSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void importEndpointDoesNotExistWhenDisabledEvenWithCorrectToken() throws Exception {
        mockMvc.perform(post("/admin/schedule-imports/replace")
                        .header("X-Import-Token", "test-only-import-token"))
                .andExpect(status().isNotFound());
    }
}
