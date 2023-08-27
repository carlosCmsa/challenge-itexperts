package com.itexperts.bank.service;

import com.itexperts.bank.model.Account;
import com.itexperts.bank.model.Card;
import com.itexperts.bank.model.TypeCard;
import com.itexperts.bank.repository.AccountRepository;
import com.itexperts.bank.repository.CardRepository;
import com.itexperts.bank.repository.TypeCardRepository;
import com.itexperts.bank.request.dto.RequestCardDto;
import com.itexperts.bank.response.dto.ResponseAccountDto;
import com.itexperts.bank.service.exception.AccountNotFoundException;
import com.itexperts.bank.service.exception.CardNotFoundException;
import com.itexperts.bank.service.exception.ExistingCardException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TypeCardRepository typeCardRepository;

    public ResponseAccountDto createCard(RequestCardDto card, Integer id) {
        //verifica se existe um usuario com o id informado
        Optional<Account> account = accountRepository.findById(id);
        account.orElseThrow(() -> new AccountNotFoundException("Não foi possível encontrar uma conta com o id informado."));

        //verifica se já existe um cartão com o número informado
        Optional<Card> cardExist = cardRepository.findByNumber(card.getNumber());
        if(cardExist.isPresent()) {
            throw new ExistingCardException("Não foi possível criar um cartão. Já existe um cartão com o 'number' informado.");
        }

        //converte o ResponseCardDto para Entity Card e adiciona o AccountId ao Card
        Card newCard = card.dtoToEntity();
        newCard.setAccountId(account.get());

        //verifica se já existe um TypeCard igual ao TypeCard passado no newCard, com base no nome ignorando caixa alta ou baixa
        Optional<TypeCard> typeCard = typeCardRepository.findByNameIgnoreCase(newCard.getTypeCard().getName());

        if(typeCard.isPresent()) {
            //caso exista adiciona o id do TypeCard existente ao id do TypeCard do newCard
            newCard.getTypeCard().setId(typeCard.get().getId());
        }else {
            //caso não exista salva esse novo TypeCard na tabela type_card
            typeCardRepository.save(newCard.getTypeCard());
        }

        //salva o newCard na tabela de Card
        cardRepository.save(newCard);

        //retorna a Account com o newCard adicionado
        return new ResponseAccountDto(account.get());
    }


    public void deleteCard(Integer idAccount, Integer idCard) {
        Optional<Account> account = accountRepository.findById(idAccount);
        account.orElseThrow(() -> new AccountNotFoundException("Não foi possível encontrar uma conta com o id informado."));

        Optional<Card> card = cardRepository.findByIdAndAccountId(idCard, account.get());
        card.orElseThrow(() -> new CardNotFoundException("Não foi possível encontrar um cartão com o id informado."));

        cardRepository.deleteById(idCard);
    }
}
