//package alstom.rms.springboot.mokitotests;
//
//import alstom.rms.springboot.BaseMockitoTests;
//import alstom.rms.springboot.controller.ContactController;
//import alstom.rms.springboot.model.Contact;
//import alstom.rms.springboot.model.Location;
//import alstom.rms.springboot.model.Station;
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
//public class ContactMockitoTests extends BaseMockitoTests {
//
//    @MockBean
//    private ContactController contactController;
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void getAllContacts() throws Exception {
//        List<Contact> contacts = Arrays.asList(
//                new Contact(UUID.randomUUID(), "John Doe", "1234567890", "john.doe@example.com", null),
//                new Contact(UUID.randomUUID(), "Jane Smith", "0987654321", "jane.smith@example.com", null)
//        );
//
//        when(contactController.getAllContacts()).thenReturn(contacts);
//
//        this.mockMvc.perform(get("/api/contacts")
//                        .header("Authorization", "Bearer dummy_token"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$").isArray())
//                .andDo(document("get-all-contacts",
//                        responseFields(
//                                fieldWithPath("[].id").description("The unique identifier of the contact"),
//                                fieldWithPath("[].contactPerson").description("The name of the contact person"),
//                                fieldWithPath("[].contactPhone").description("The phone number of the contact"),
//                                fieldWithPath("[].contactEmail").description("The email address of the contact"),
//                                fieldWithPath("[].station").description("The associated station (null if not provided)")
//                        )));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void getContactById() throws Exception {
//        UUID contactId = UUID.randomUUID();
//        Contact contact = new Contact(contactId, "John Doe", "1234567890", "john.doe@example.com", null);
//
//        when(contactController.getContactById(contactId)).thenReturn(ResponseEntity.ok(contact));
//
//        this.mockMvc.perform(get("/api/contacts/{id}", contactId)
//                        .header("Authorization", "Bearer dummy_token"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json"))
//                .andExpect(jsonPath("$.id").value(contactId.toString()))
//                .andExpect(jsonPath("$.contactPerson").value("John Doe"))
//                .andExpect(jsonPath("$.contactPhone").value("1234567890"))
//                .andExpect(jsonPath("$.contactEmail").value("john.doe@example.com"))
//                .andDo(document("get-contact-by-id",
//                        responseFields(
//                                fieldWithPath("id").description("The unique identifier of the contact"),
//                                fieldWithPath("contactPerson").description("The name of the contact person"),
//                                fieldWithPath("contactPhone").description("The phone number of the contact"),
//                                fieldWithPath("contactEmail").description("The email address of the contact"),
//                                fieldWithPath("station").description("The associated station (null if not provided)")
//                        )));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void createContact() throws Exception {
//        UUID contactId = UUID.randomUUID();
//        Contact contact = new Contact(contactId, "John Doe", "1234567890", "john.doe@example.com", null);
//
//        when(contactController.createContact(any(Contact.class))).thenReturn(contact);
//
//        String requestPayload = """
//                {
//                    "contactPerson": "John Doe",
//                    "contactPhone": "1234567890",
//                    "contactEmail": "john.doe@example.com"
//                }
//                """;
//
//        this.mockMvc.perform(post("/api/contacts")
//                        .contentType("application/json")
//                        .header("Authorization", "Bearer dummy_token")
//                        .content(requestPayload))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(contactId.toString()))
//                .andExpect(jsonPath("$.contactPerson").value("John Doe"))
//                .andExpect(jsonPath("$.contactPhone").value("1234567890"))
//                .andExpect(jsonPath("$.contactEmail").value("john.doe@example.com"))
//                .andDo(document("create-contact",
//                        requestFields(
//                                fieldWithPath("contactPerson").description("The name of the contact person"),
//                                fieldWithPath("contactPhone").description("The phone number of the contact"),
//                                fieldWithPath("contactEmail").description("The email address of the contact")
//                        ),
//                        responseFields(
//                                fieldWithPath("id").description("The unique identifier of the contact"),
//                                fieldWithPath("contactPerson").description("The name of the contact person"),
//                                fieldWithPath("contactPhone").description("The phone number of the contact"),
//                                fieldWithPath("contactEmail").description("The email address of the contact"),
//                                fieldWithPath("station").description("The associated station (null if not provided)")
//                        )));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void updateContactById() throws Exception {
//        UUID contactId = UUID.randomUUID();
//
//        Location location = new Location(UUID.randomUUID(), "New York", "NY", "10001");
//        Station station = new Station(UUID.fromString("455f6581-d95d-4a94-823b-946b57dca44d"), "ST001", "Station Name", location, null);
//        Contact existingContact = new Contact(contactId, "Jane Smith", "0987654321", "jane.smith@example.com", station);
//        Contact updatedContact = new Contact(contactId, "Jane Smiath", "098765s3213", "jane.smitashq@lausunion.com", station);
//
//        when(contactController.getContactById(contactId)).thenReturn(ResponseEntity.ok(existingContact));
//        when(contactController.updateContactById(eq(contactId), any(Contact.class))).thenReturn(ResponseEntity.ok(updatedContact));
//
//        String requestPayload = """
//                {
//                    "contactPerson": "Jane Smiath",
//                    "contactPhone": "098765s3213",
//                    "contactEmail": "jane.smitashq@lausunion.com",
//                    "station": {
//                        "id": "455f6581-d95d-4a94-823b-946b57dca44d"
//                    }
//                }
//                """;
//
//        this.mockMvc.perform(put("/api/contacts/{id}", contactId)
//                        .contentType("application/json")
//                        .header("Authorization", "Bearer dummy_token")
//                        .content(requestPayload))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(contactId.toString()))
//                .andExpect(jsonPath("$.contactPerson").value("Jane Smiath"))
//                .andExpect(jsonPath("$.contactPhone").value("098765s3213"))
//                .andExpect(jsonPath("$.contactEmail").value("jane.smitashq@lausunion.com"))
//                .andExpect(jsonPath("$.station.id").value("455f6581-d95d-4a94-823b-946b57dca44d"))
//                .andDo(document("update-contact",
//                        requestFields(
//                                fieldWithPath("contactPerson").description("The contact person's name"),
//                                fieldWithPath("contactPhone").description("The contact person's phone number"),
//                                fieldWithPath("contactEmail").description("The contact person's email address"),
//                                fieldWithPath("station.id").description("The ID of the associated station")
//                        ),
//                        responseFields(
//                                fieldWithPath("id").description("The unique identifier of the contact"),
//                                fieldWithPath("contactPerson").description("The contact person's name"),
//                                fieldWithPath("contactPhone").description("The contact person's phone number"),
//                                fieldWithPath("contactEmail").description("The contact person's email address"),
//                                fieldWithPath("station.id").description("The ID of the associated station")
//                        )));
//
//    }
//
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void deleteContactById() throws Exception {
//        UUID contactId = UUID.randomUUID();
//        String expectedMessage = "Contact successfully deleted.";
//
//        when(contactController.deleteContactById(contactId)).thenReturn(ResponseEntity.ok(expectedMessage));
//
//        this.mockMvc.perform(delete("/api/contacts/{id}", contactId)
//                        .header("Authorization", "Bearer dummy_token"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedMessage))
//                .andDo(document("delete-contact-by-id",
//                        requestHeaders(
//                                headerWithName("Authorization").description("Bearer token for authentication")
//                        ),
//                        responseBody()));
//    }
//
//}
