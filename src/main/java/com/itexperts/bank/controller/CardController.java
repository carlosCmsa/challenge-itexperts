package com.itexperts.bank.controller;

import com.itexperts.bank.request.dto.RequestCardDto;
import com.itexperts.bank.response.dto.ResponseAccountDto;
import com.itexperts.bank.service.AccountService;
import com.itexperts.bank.service.CardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("api/v1/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private AccountService accountService;

    @ApiOperation("cria um novo cartão para uma conta existente.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created" ),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @PostMapping("/{idAccount}")
    public ResponseEntity<Void> createCard(@Valid @RequestBody RequestCardDto card, @PathVariable Integer idAccount){
        ResponseAccountDto accountDto = cardService.createCard(card, idAccount);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(accountDto.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @ApiOperation("deleta um cartão de uma conta existente.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content" ),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @DeleteMapping("/{idAccountId}/{idCard}")
    public ResponseEntity<Void> deleteCard(@PathVariable Integer idAccountId, @PathVariable Integer idCard){
        cardService.deleteCard(idAccountId, idCard);

        return ResponseEntity.noContent().build();
    }
}
