package com.dreamgames.backendengineeringcasestudy.controller;

import com.dreamgames.backendengineeringcasestudy.model.Partnership;
import com.dreamgames.backendengineeringcasestudy.service.PartnershipService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartnershipController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({MockitoExtension.class})
class PartnershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartnershipService partnershipService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void acceptPartnership() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/partnerships/accept/1/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(partnershipService).acceptPartnership(1L, 2L);
    }

    @Test
    void rejectPartnership() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/partnerships/reject/1/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(partnershipService).rejectPartnership(1L, 2L);
    }

    @Test
    void endPartnership() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/partnerships/cancel/1/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(partnershipService).endPartnership(1L, 2L);
    }

    @Test
    void resetAllPartnerships() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/partnerships/reset")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(partnershipService).resetAllPartnerships();
    }

    @Test
    void getPendingPartnerships() throws Exception {
        Partnership partnership = new Partnership();
        List<Partnership> partnerships = Collections.singletonList(partnership);
        Mockito.when(partnershipService.getPendingPartnerships(anyLong())).thenReturn(partnerships);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/partnerships/pending/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = objectMapper.writeValueAsString(partnerships);

        assertEquals(expectedResponseBody, actualResponseBody);
        verify(partnershipService).getPendingPartnerships(2L);
    }
}