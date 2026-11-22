package ua.edu.ukma.unischeduler.feature.scheduleimport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "unischeduler.import.enabled=true",
        "UNISCHEDULER_IMPORT_TOKEN=test-only-import-token"
})
@AutoConfigureMockMvc
class ScheduleImportSecurityIntegrationTest {
    private static final String TOKEN = "test-only-import-token";

    private static final String VALID_IMPORT = """
            {
              "specialityName": "Software Engineering",
              "faculty": "Informatics",
              "department": "Software Engineering",
              "academicYear": "2026-2027",
              "studyYear": 4,
              "semester": "AUTUMN",
              "occurrences": [{
                "sourceKey": "test-occurrence-1",
                "courseName": "Parallel Programming",
                "teacher": "Professor Example",
                "group": "1",
                "location": "Room 101",
                "description": "Lecture",
                "startsAt": "2026-09-07T10:00:00",
                "endsAt": "2026-09-07T11:20:00",
                "sourceRow": 12,
                "sourceMetadata": "{}"
              }]
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    // Test the HTTP security boundary without persisting any schedule data.
    @MockitoBean
    private ScheduleImportService service;

    @Test
    void correctTokenAllowsImportWithoutAnOidcSession() throws Exception {
        when(service.replace(any(ScheduleImportRequest.class)))
                .thenReturn(new ScheduleImportResult(1L, 1, 1, 1, 0, 0, 0, 0));

        mockMvc.perform(post("/admin/schedule-imports/replace")
                        .header("X-Import-Token", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_IMPORT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classesCreated").value(1));

        verify(service).replace(any(ScheduleImportRequest.class));
    }

    @Test
    void missingTokenIsRejectedBeforeImport() throws Exception {
        mockMvc.perform(post("/admin/schedule-imports/replace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_IMPORT))
                .andExpect(status().isForbidden());

        verify(service, never()).replace(any(ScheduleImportRequest.class));
    }

    @Test
    void incorrectTokenIsRejectedBeforeImport() throws Exception {
        mockMvc.perform(post("/admin/schedule-imports/replace")
                        .header("X-Import-Token", "wrong-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_IMPORT))
                .andExpect(status().isForbidden());

        verify(service, never()).replace(any(ScheduleImportRequest.class));
    }

    @Test
    void importTokenDoesNotGrantAdminPrivilegesToOtherEndpoints() throws Exception {
        mockMvc.perform(post("/speciality")
                        .header("X-Import-Token", TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Protected Speciality\"}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/user").header("X-Import-Token", TOKEN))
                .andExpect(status().isUnauthorized());

        verify(service, never()).replace(any(ScheduleImportRequest.class));
    }
}
