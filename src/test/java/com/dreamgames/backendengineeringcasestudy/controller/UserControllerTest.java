package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.model.User;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({MockitoExtension.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser() throws Exception {
        User user = new User();
        Mockito.when(userService.createUser()).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/create-user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(user);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).createUser();
    }

    @Test
    void sendInvitation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/send-invitation/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).sendInvitation(1L);
    }

    @Test
    void claimReward() throws Exception {
        User user = new User();
        Mockito.when(userService.claimReward(anyLong())).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/claim-reward/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(user);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).claimReward(1L);
    }

    @Test
    void getInvitations() throws Exception {
        Partnership partnership = new Partnership();
        Mockito.when(userService.getInvitations(anyLong())).thenReturn(partnership);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/get-invitations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(partnership);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).getInvitations(1L);
    }

    @Test
    void getSuggestions() throws Exception {
        User user = new User();
        List<User> users = Collections.singletonList(user);
        Mockito.when(userService.getSuggestions(anyLong())).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/get-suggestions/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(users);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).getSuggestions(1L);
    }

    @Test
    void getBalloonsInfo() throws Exception {
        Map<String, Object> balloonsInfo = Map.of("inflatingScore", 50, "inflationThreshold", 100, "unusedHeliumCount", 100);
        Mockito.when(userService.getBalloonsInfo(anyLong())).thenReturn(balloonsInfo);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/get/balloons-info/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(balloonsInfo);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).getBalloonsInfo(1L);
    }

    @Test
    void getLeaderboard() throws Exception {
        User user = new User();
        List<Map<String, Object>> users = Collections.singletonList(Map.of("level", 1, "coins", 100));
        Mockito.when(userService.getLeaderboard()).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/get-leaderboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(users);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).getLeaderboard();
    }

    @Test
    void updateUserLevel() throws Exception {
        Map<String, Object> response = Map.of("level", 2, "coins", 200);
        Mockito.when(userService.updateUserLevel(anyLong())).thenReturn(response);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/level-up/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(response);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).updateUserLevel(1L);
    }

    @Test
    void updateUserCoin() throws Exception {
        User user = new User();
        Mockito.when(userService.updateUserCoin(anyLong(), anyInt())).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/update-coins/1/50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(user);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).updateUserCoin(1L, 50);
    }

    @Test
    void updateUserHeliumCount() throws Exception {
        User user = new User();
        Mockito.when(userService.updateUserHeliumCount(anyLong(), anyInt())).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/update-helium-count/1/50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(user);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).updateUserHeliumCount(1L, 50);
    }

    @Test
    void updateBalloonProgress() throws Exception {
        Partnership partnership = new Partnership();
        Mockito.when(userService.updateBalloonProgress(anyLong(), anyInt())).thenReturn(partnership);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/update-balloon-progress/1/50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(partnership);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).updateBalloonProgress(1L, 50);
    }

    @Test
    void setUserLevel() throws Exception {
        User user = new User();
        Mockito.when(userService.setUserLevel(anyLong(), anyInt())).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/set-level/1/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(user);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(userService).setUserLevel(1L, 10);
    }
}