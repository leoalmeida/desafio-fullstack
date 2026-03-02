package com.example.backend.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.example.backend.exception.TesteIntegradoException;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.TimeZone;

@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@EnableConfigurationProperties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) 
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    protected void startMockMvc(@NonNull WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    protected final ObjectMapper mapper = new ObjectMapper()
            .setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    protected <T> List<T> performGetAllRequest(@NonNull String path, @NonNull Class<T> responseType, ResultMatcher... matchers)
            throws TesteIntegradoException, Exception {
        
        MvcResult mvcResult = 
                    submitGetAction(path)
                            .andExpectAll(matchers)
                            .andReturn();
        if (mvcResult == null) throw new TesteIntegradoException("Erro ao executar cenário de consulta");
        return  convertStringToListClass(mvcResult.getResponse().getContentAsString(), responseType);
    }

    protected <T> T performGetRequest(@NonNull String path, Long keyParameter, @NonNull Class<T> responseType, ResultMatcher... matchers)
            throws TesteIntegradoException, Exception {
        
        MvcResult mvcResult = submitGetAction(path, keyParameter)
                            .andExpectAll(matchers)
                            .andReturn();
        if (mvcResult == null) throw new TesteIntegradoException("Erro ao executar cenário de consulta");
        return  convertStringToClass(mvcResult.getResponse().getContentAsString(), responseType);
    }

    protected String performDeleteRequest(@NonNull String path, Long keyParameter, ResultMatcher... matchers)
            throws TesteIntegradoException, Exception {
        
        MvcResult mvcResult = submitDeleteAction(path, keyParameter)
                .andExpectAll(matchers)
                .andReturn();
        
        if (mvcResult == null) throw new TesteIntegradoException("Erro ao executar cenário de delete");
        return mvcResult.getResponse().getErrorMessage();
    }
    protected <T> T performPutRequest(@NonNull String path, Long keyParameter, Object body, Class<T> responseType, ResultMatcher... matchers)
            throws TesteIntegradoException, Exception {
        
        MvcResult mvcResult = submitPutAction(path, body, keyParameter)
                .andExpectAll(matchers)
                .andReturn();
    
        if (mvcResult == null) throw new TesteIntegradoException("Erro ao executar cenário de update");
        if (responseType == null) return null;
        return convertStringToClass(mvcResult.getResponse().getContentAsString(), responseType);
    }

    protected <T> T performPostRequest(@NonNull String path, Object object, Class<T> responseType, ResultMatcher... matchers)
            throws TesteIntegradoException, Exception {
        MvcResult mvcResult = submitPostAction(path, object)
                .andExpectAll(matchers)
                .andReturn();
        if (mvcResult == null) throw new TesteIntegradoException("Erro ao executar cenário de criação");
        if (responseType == null) return null;
        return convertStringToClass(mvcResult.getResponse().getContentAsString(), responseType);
    }

    protected <T> T performPostRequestExpectedServerError(@NonNull String path, Object object, Class<T> responseType)
            throws Exception {
        return performPostRequest(path, object, responseType, status().is5xxServerError());
    }

    private ResultActions submitGetAction(@NonNull String path) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(path));
    }

    private ResultActions submitGetAction(@NonNull String path, Object... parameters) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(path, parameters));
    }

    private ResultActions submitDeleteAction(@NonNull String path, Object... parameters) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.delete(path, parameters));
    }

    private ResultActions submitPutAction(@NonNull String path, Object body, Object... parameters) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.put(path, parameters)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(body)));
    }

    private ResultActions submitPostAction(@NonNull String path, Object object) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(object)));
    }

    private <T> T convertStringToClass(String jsonString, Class<T> responseType) throws JsonProcessingException {
        return mapper.readValue(jsonString, responseType);
    }
    private <T> List<T> convertStringToListClass(String jsonString, Class<T> responseType) throws JsonProcessingException {
        return mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(List.class, responseType));
    }
}