package alstom.rms.springboot.mokitotests;

import alstom.rms.springboot.BaseMockitoTests;
import alstom.rms.springboot.controller.LocationController;
import alstom.rms.springboot.model.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
public class LocationMockitoTests extends BaseMockitoTests {

    @MockBean
    private LocationController locationController;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void getAllLocations() throws Exception {

        List<Location> locations = Arrays.asList(
                new Location(UUID.fromString("80d3b620-f83b-4e6f-ab3a-16e307a63111"), "New York", "NY", "10001"),
                new Location(UUID.fromString("6d27c8f7-0153-4650-b187-14db89742111"), "Los Angeles", "CA", "90001")
        );
        when(locationController.getAllLocations()).thenReturn(locations);

        this.mockMvc.perform(get("/api/locations")
                        .header("Authorization", "Bearer dummy_token") // Add Authorization header here
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andDo(document("get-all-locations",
                        responseFields(
                                fieldWithPath("[].id").description("The unique identifier of the location"),
                                fieldWithPath("[].city").description("The city of the location"),
                                fieldWithPath("[].state").description("The state of the location"),
                                fieldWithPath("[].zipCode").description("The zip code of the location")
                        )));

    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void getLocationById() throws Exception {
        UUID id = UUID.randomUUID();
        Location location = new Location(id, "New York", "NY", "10001");
        when(locationController.getLocationById(id)).thenReturn(ResponseEntity.ok(location));

        this.mockMvc.perform(get("/api/locations/{id}", id)
                        .header("Authorization", "Bearer dummy_token")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.city").value("New York"))
                .andExpect(jsonPath("$.state").value("NY"))
                .andExpect(jsonPath("$.zipCode").value("10001"))
                .andDo(document("get-location-by-id",
                        responseFields(
                                fieldWithPath("id").description("The unique identifier of the location"),
                                fieldWithPath("city").description("The city of the location"),
                                fieldWithPath("state").description("The state of the location"),
                                fieldWithPath("zipCode").description("The zip code of the location")
                        )));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createLocation() throws Exception {
        UUID generatedId = UUID.randomUUID();
        Location mockLocation = new Location(generatedId, "Chicago", "IL", "60601");

        when(locationController.createLocation(any(Location.class))).thenReturn(mockLocation);

        String requestPayload = """
                {
                    "city": "Chicago",
                    "state": "IL",
                    "zipCode": "60601"
                }
                """;

        this.mockMvc.perform(post("/api/locations")
                        .contentType("application/json")
                        .header("Authorization", "Bearer dummy_token")
                        .content(requestPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(generatedId.toString()))
                .andExpect(jsonPath("$.city").value("Chicago"))
                .andExpect(jsonPath("$.state").value("IL"))
                .andExpect(jsonPath("$.zipCode").value("60601"))
                .andDo(document("create-location",
                        requestFields(
                                fieldWithPath("city").description("The city of the location"),
                                fieldWithPath("state").description("The state of the location"),
                                fieldWithPath("zipCode").description("The zip code of the location")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The unique identifier of the location"),
                                fieldWithPath("city").description("The city of the location"),
                                fieldWithPath("state").description("The state of the location"),
                                fieldWithPath("zipCode").description("The zip code of the location")
                        )));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateLocationById() throws Exception {
        UUID locationId = UUID.randomUUID();
        Location existingLocation = new Location(locationId, "Chicago", "IL", "60601");
        Location updatedLocation = new Location(locationId, "Chicago Updated", "IL", "60602");

        when(locationController.getLocationById(locationId)).thenReturn(ResponseEntity.ok(existingLocation));
        when(locationController.updateLocationById(eq(locationId), any(Location.class))).thenReturn(ResponseEntity.ok(updatedLocation));

        String requestPayload = """
                {
                    "city": "Chicago Updated",
                    "state": "IL",
                    "zipCode": "60602"
                }
                """;

        this.mockMvc.perform(put("/api/locations/{id}", locationId)
                        .contentType("application/json")
                        .header("Authorization", "Bearer dummy_token")
                        .content(requestPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(locationId.toString()))
                .andExpect(jsonPath("$.city").value("Chicago Updated"))
                .andExpect(jsonPath("$.state").value("IL"))
                .andExpect(jsonPath("$.zipCode").value("60602"))
                .andDo(document("update-location",
                        requestFields(
                                fieldWithPath("city").description("The updated city of the location"),
                                fieldWithPath("state").description("The updated state of the location"),
                                fieldWithPath("zipCode").description("The updated zip code of the location")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The unique identifier of the location"),
                                fieldWithPath("city").description("The updated city of the location"),
                                fieldWithPath("state").description("The updated state of the location"),
                                fieldWithPath("zipCode").description("The updated zip code of the location")
                        )));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteLocationById() throws Exception {
        UUID id = UUID.randomUUID();
        String expectedMessage = "Location data successfully deleted.";

        when(locationController.deleteLocationById(id)).thenReturn(ResponseEntity.ok(expectedMessage));

        this.mockMvc.perform(delete("/api/locations/{id}", id)
                        .header("Authorization", "Bearer dummy_token"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage))
                .andDo(document("delete-location-by-id",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token for authentication")
                        ),
                        responseBody()
                ));
    }
}
