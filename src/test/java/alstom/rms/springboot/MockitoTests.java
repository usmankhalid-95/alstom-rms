package alstom.rms.springboot;

import alstom.rms.springboot.controller.TrainController;
import alstom.rms.springboot.model.Train;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
public class MockitoTests {

    private MockMvc mockMvc;
    @MockBean
    private TrainController trainController;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getAllTrains() throws Exception {

        List<Train> trains = Arrays.asList(
                new Train(UUID.fromString("80d3b620-f83b-4e6f-ab3a-16e307a63111"), "B6", 100),
                new Train(UUID.fromString("6d27c8f7-0153-4650-b187-14db89742111"), "C7", 300)
        );
        when(trainController.getAllTrains()).thenReturn(trains);

        this.mockMvc.perform(get("/api/trains")
                        .header("Authorization", "Bearer dummy_token") // Add Authorization header here
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andDo(document("get-all-trains",
                        responseFields(
                                fieldWithPath("[].id").description("The unique identifier of the train"),
                                fieldWithPath("[].trainNumber").description("The train number"),
                                fieldWithPath("[].seatCapacity").description("The seat capacity of the train")
                        )));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void getTrainById() throws Exception {
        UUID id = UUID.randomUUID();
        Train train = new Train(id, "B6", 100);
        when(trainController.getTrainById(id)).thenReturn(ResponseEntity.ok(train));

        this.mockMvc.perform(get("/api/trains/{id}", id)
                        .header("Authorization", "Bearer dummy_token")  // Add Authorization header here
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.trainNumber").value("B6"))
                .andExpect(jsonPath("$.seatCapacity").value(100))
                .andDo(document("get-train-by-id",
                        responseFields(
                                fieldWithPath("id").description("The unique identifier of the train"),
                                fieldWithPath("trainNumber").description("The train number"),
                                fieldWithPath("seatCapacity").description("The seat capacity of the train")
                        )));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void createTrain() throws Exception {
        UUID generatedId = UUID.randomUUID();
        Train mockTrain = new Train(generatedId, "D8", 200);

        when(trainController.createTrain(any(Train.class))).thenReturn(mockTrain);

        String requestPayload = """
                {
                    "trainNumber": "D8",
                    "seatCapacity": 200
                }
                """;

        this.mockMvc.perform(post("/api/trains")
                        .contentType("application/json")
                        .header("Authorization", "Bearer dummy_token")
                        .content(requestPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(generatedId.toString()))
                .andExpect(jsonPath("$.trainNumber").value("D8"))
                .andExpect(jsonPath("$.seatCapacity").value(200))
                .andDo(document("create-train",
                        requestFields(
                                fieldWithPath("trainNumber").description("The train number"),
                                fieldWithPath("seatCapacity").description("The seat capacity of the train")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The unique identifier of the train"),
                                fieldWithPath("trainNumber").description("The train number"),
                                fieldWithPath("seatCapacity").description("The seat capacity of the train")
                        )));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void updateTrainById() throws Exception {
        UUID trainId = UUID.randomUUID();
        Train existingTrain = new Train(trainId, "B6", 100);
        Train updatedTrain = new Train(trainId, "B6 Updated", 250);

        when(trainController.getTrainById(trainId)).thenReturn(ResponseEntity.ok(existingTrain));

        when(trainController.updateTrainById(eq(trainId), any(Train.class))).thenReturn(ResponseEntity.ok(updatedTrain));

        String requestPayload = """
                {
                    "trainNumber": "B6 Updated",
                    "seatCapacity": 250
                }
                """;

        this.mockMvc.perform(put("/api/trains/{id}", trainId)
                        .contentType("application/json")
                        .header("Authorization", "Bearer dummy_token")
                        .content(requestPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(trainId.toString()))
                .andExpect(jsonPath("$.trainNumber").value("B6 Updated"))
                .andExpect(jsonPath("$.seatCapacity").value(250))
                .andDo(document("update-train",
                        requestFields(
                                fieldWithPath("trainNumber").description("The updated train number"),
                                fieldWithPath("seatCapacity").description("The updated seat capacity of the train")
                        ),
                        responseFields(
                                fieldWithPath("id").description("The unique identifier of the train"),
                                fieldWithPath("trainNumber").description("The updated train number"),
                                fieldWithPath("seatCapacity").description("The updated seat capacity of the train")
                        )));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteTrainById() throws Exception {
        UUID id = UUID.randomUUID();
        String expectedMessage = "B07 train data successfully deleted.";

        when(trainController.deleteTrainById(id)).thenReturn(ResponseEntity.ok(expectedMessage));

//        this.mockMvc.perform(delete("/api/trains/{id}", id)
//                        .header("Authorization", "Bearer dummy_token")
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().string("B77 data successfully deleted."))
//                .andDo(document("delete-train",
//                        responseFields(
//                                fieldWithPath("response").description("The deletion confirmation message")
//                        )
//                ));
        this.mockMvc.perform(delete("/api/trains/{id}", id)
                        .header("Authorization", "Bearer dummy_token"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage)) // Expecting plain text
                .andDo(document("delete-train-by-id", // Update the documentation
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token for authentication")
                        ),
                        responseBody()
                ));
    }

//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    public void deleteTrainById() throws Exception {
//        UUID id = UUID.randomUUID();
//        String expectedMessage = "Train successfully deleted";
//
//        // Mocking the behavior of the delete API
//        when(trainController.deleteTrainById(id)).thenReturn(ResponseEntity.ok(expectedMessage));
//
//        // Performing the DELETE request and validating response
//        this.mockMvc.perform(delete("/api/trains/{id}", id)
//                        .header("Authorization", "Bearer dummy_token"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedMessage)) // Expecting plain text
//                .andDo(document("delete-train-by-id", // Update the documentation
//                        requestHeaders(
//                                headerWithName("Authorization").description("Bearer token for authentication")
//                        ),
//                        responseBody() // Document the plain text response body
//                ));
//    }


}