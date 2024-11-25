//package alstom.rms.springboot.mokitotests;
//
//import alstom.rms.springboot.BaseMockitoTests;
//import alstom.rms.springboot.controller.PassengerController;
//import alstom.rms.springboot.model.Passenger;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.ResponseEntity;
//import org.springframework.restdocs.RestDocumentationExtension;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
//import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
//@SpringBootTest
//public class PassengerMockitoTests extends BaseMockitoTests {
//
//    @MockBean
//    private PassengerController passengerController;
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    public void getAllPassengers() throws Exception {
//        List<Passenger> passengers = Arrays.asList(
//                new Passenger(UUID.randomUUID(), "John Doe", "1234567890", "john@example.com"),
//                new Passenger(UUID.randomUUID(), "Jane Doe", "0987654321", "jane@example.com")
//        );
//        when(passengerController.getAllPassengers()).thenReturn(passengers);
//
//        this.mockMvc.perform(get("/api/passengers")
//                        .header("Authorization", "Bearer dummy_token"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$").isArray())
//                .andDo(document("get-all-passengers",
//                        responseFields(
//                                fieldWithPath("[].id").description("The unique identifier of the passenger"),
//                                fieldWithPath("[].name").description("The name of the passenger"),
//                                fieldWithPath("[].contactPhone").description("The contact phone number of the passenger"),
//                                fieldWithPath("[].contactEmail").description("The contact email of the passenger")
//                        )));
//    }
//
//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    public void getPassengerById() throws Exception {
//        UUID id = UUID.randomUUID();
//        Passenger passenger = new Passenger(id, "John Doe", "1234567890", "john@example.com");
//        when(passengerController.getPassengerById(id)).thenReturn(ResponseEntity.ok(passenger));
//
//        this.mockMvc.perform(get("/api/passengers/{id}", id)
//                        .header("Authorization", "Bearer dummy_token"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.id").value(id.toString()))
//                .andExpect(jsonPath("$.name").value("John Doe"))
//                .andExpect(jsonPath("$.contactPhone").value("1234567890"))
//                .andExpect(jsonPath("$.contactEmail").value("john@example.com"))
//                .andDo(document("get-passenger-by-id",
//                        responseFields(
//                                fieldWithPath("id").description("The unique identifier of the passenger"),
//                                fieldWithPath("name").description("The name of the passenger"),
//                                fieldWithPath("contactPhone").description("The contact phone number of the passenger"),
//                                fieldWithPath("contactEmail").description("The contact email of the passenger")
//                        )));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void createPassenger() throws Exception {
//        UUID generatedId = UUID.randomUUID();
//        Passenger mockPassenger = new Passenger(generatedId, "John Doe", "1234567890", "john@example.com");
//
//        when(passengerController.createPassenger(any(Passenger.class))).thenReturn(mockPassenger);
//
//        String requestPayload = """
//                {
//                    "name": "John Doe",
//                    "contactPhone": "1234567890",
//                    "contactEmail": "john@example.com"
//                }
//                """;
//
//        this.mockMvc.perform(post("/api/passengers")
//                        .contentType("application/json")
//                        .header("Authorization", "Bearer dummy_token")
//                        .content(requestPayload))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(generatedId.toString()))
//                .andExpect(jsonPath("$.name").value("John Doe"))
//                .andExpect(jsonPath("$.contactPhone").value("1234567890"))
//                .andExpect(jsonPath("$.contactEmail").value("john@example.com"))
//                .andDo(document("create-passenger",
//                        requestFields(
//                                fieldWithPath("name").description("The name of the passenger"),
//                                fieldWithPath("contactPhone").description("The contact phone number of the passenger"),
//                                fieldWithPath("contactEmail").description("The contact email of the passenger")
//                        ),
//                        responseFields(
//                                fieldWithPath("id").description("The unique identifier of the passenger"),
//                                fieldWithPath("name").description("The name of the passenger"),
//                                fieldWithPath("contactPhone").description("The contact phone number of the passenger"),
//                                fieldWithPath("contactEmail").description("The contact email of the passenger")
//                        )));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void updatePassengerById() throws Exception {
//        UUID passengerId = UUID.randomUUID();
//        Passenger existingPassenger = new Passenger(passengerId, "John Doe", "1234567890", "john@example.com");
//        Passenger updatedPassenger = new Passenger(passengerId, "John Doe Updated", "1112223333", "john.updated@example.com");
//
//        when(passengerController.getPassengerById(passengerId)).thenReturn(ResponseEntity.ok(existingPassenger));
//        when(passengerController.updatePassengerById(eq(passengerId), any(Passenger.class))).thenReturn(ResponseEntity.ok(updatedPassenger));
//
//        String requestPayload = """
//                {
//                    "name": "John Doe Updated",
//                    "contactPhone": "1112223333",
//                    "contactEmail": "john.updated@example.com"
//                }
//                """;
//
//        this.mockMvc.perform(put("/api/passengers/{id}", passengerId)
//                        .contentType("application/json")
//                        .header("Authorization", "Bearer dummy_token")
//                        .content(requestPayload))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(passengerId.toString()))
//                .andExpect(jsonPath("$.name").value("John Doe Updated"))
//                .andExpect(jsonPath("$.contactPhone").value("1112223333"))
//                .andExpect(jsonPath("$.contactEmail").value("john.updated@example.com"))
//                .andDo(document("update-passenger",
//                        requestFields(
//                                fieldWithPath("name").description("The updated name of the passenger"),
//                                fieldWithPath("contactPhone").description("The updated contact phone number of the passenger"),
//                                fieldWithPath("contactEmail").description("The updated contact email of the passenger")
//                        ),
//                        responseFields(
//                                fieldWithPath("id").description("The unique identifier of the passenger"),
//                                fieldWithPath("name").description("The updated name of the passenger"),
//                                fieldWithPath("contactPhone").description("The updated contact phone number of the passenger"),
//                                fieldWithPath("contactEmail").description("The updated contact email of the passenger")
//                        )));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void deletePassengerById() throws Exception {
//        UUID id = UUID.randomUUID();
//        String expectedMessage = "Passenger data successfully deleted.";
//
//        when(passengerController.deletePassengerById(id)).thenReturn(ResponseEntity.ok(expectedMessage));
//
//        this.mockMvc.perform(delete("/api/passengers/{id}", id)
//                        .header("Authorization", "Bearer dummy_token"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedMessage))
//                .andDo(document("delete-passenger-by-id",
//                        requestHeaders(
//                                headerWithName("Authorization").description("Bearer token for authentication")
//                        ),
//                        responseBody()
//                ));
//    }
//}
