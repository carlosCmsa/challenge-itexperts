package com.itexperts.bank.controller;

import com.itexperts.bank.enums.Flag;
import com.itexperts.bank.request.dto.RequestAccountDto;
import com.itexperts.bank.request.dto.RequestCardDto;
import com.itexperts.bank.request.dto.RequestTypeCardDto;
import com.itexperts.bank.response.dto.ResponseAccountDto;
import com.itexperts.bank.response.dto.ResponseCardDto;
import com.itexperts.bank.service.AccountService;
import com.itexperts.bank.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CardControllerTest {

    @MockBean
    CardService cardService;

    @Autowired
    CardController cardController;

    private final Integer ID = 1;
    private final String NAME_OWNER = "Francisco";
    private final String AGENCY_CODE = "3421";
    private final String ACCOUNT_CODE = "85742586";
    private final String DIGIT_VERIFICATION = "2";
    private final String REGISTER_ID = "45724587451";

    private final String CARD_NAME = "Visa Gold";
    private final Flag FLAG = Flag.VISA;
    private final String NUMBER = "4587254875481254";
    private final String DIGIT_CODE = "458";
    private final Double LIMIT_BALANCE = 200.00;
    private final String NAME_TYPE_CARD = "Visa Gold";

    private ResponseAccountDto responseAccountDto;
    private RequestCardDto requestCardDto;

    @BeforeEach
    public void setup() {
        startResponseAccountDto();
        startRequestCardDto();
    }

    @Test
    public void whenCreateCardShouldReturnStatusCodeCreated() {
        when(cardService.createCard(requestCardDto, ID)).thenReturn(responseAccountDto);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));

        ResponseEntity<Void> response = cardController.createCard(requestCardDto, ID);

        assertFalse(response.hasBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().get("location"));
    }

    @Test
    public void whenDeleteCardShouldReturnStatusCodeNoContent() {
        ResponseEntity<Void> response = cardController.deleteCard(ID, ID);

        assertFalse(response.hasBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    public void startResponseAccountDto() {
        responseAccountDto = new ResponseAccountDto(ID, NAME_OWNER, AGENCY_CODE, ACCOUNT_CODE, DIGIT_VERIFICATION, REGISTER_ID, new ArrayList<ResponseCardDto>());
    }

    public void startRequestCardDto() {
        requestCardDto = new RequestCardDto(CARD_NAME, FLAG, NUMBER, DIGIT_CODE, LIMIT_BALANCE, new RequestTypeCardDto(NAME_TYPE_CARD));
    }
}
