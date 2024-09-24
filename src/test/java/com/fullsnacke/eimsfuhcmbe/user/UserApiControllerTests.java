package com.fullsnacke.eimsfuhcmbe.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullsnacke.eimsfuhcmbe.configuration.AppConfig;
import com.fullsnacke.eimsfuhcmbe.controller.UserController;
import com.fullsnacke.eimsfuhcmbe.dto.request.UserRequestDTO;
import com.fullsnacke.eimsfuhcmbe.entity.User;
import com.fullsnacke.eimsfuhcmbe.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = AppConfig.class)
public class UserApiControllerTests {
    private static final String END_POINT_PATH = "/users";

    @Autowired
    MockMvc mockMvc; //Send http request to RestController;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceImpl userServiceImpl; // Create Temp userService

    @Test
    public void testAddShouldReturn400BadRequest() throws Exception{
        UserRequestDTO userRequestDTO = new UserRequestDTO();

        String bodyContent = objectMapper.writeValueAsString(userRequestDTO);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest());
    }

}
