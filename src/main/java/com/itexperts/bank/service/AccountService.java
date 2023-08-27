package com.itexperts.bank.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.itexperts.bank.model.Account;
import com.itexperts.bank.model.Card;
import com.itexperts.bank.model.TypeCard;
import com.itexperts.bank.repository.AccountRepository;
import com.itexperts.bank.repository.CardRepository;
import com.itexperts.bank.repository.TypeCardRepository;
import com.itexperts.bank.request.dto.RequestAccountDto;
import com.itexperts.bank.request.dto.RequestCardDto;
import com.itexperts.bank.response.dto.ResponseAccountDto;
import com.itexperts.bank.service.exception.AccountActiveCardsException;
import com.itexperts.bank.service.exception.AccountNotFoundException;
import com.itexperts.bank.service.exception.CardNotFoundException;
import com.itexperts.bank.service.exception.ExistingAccountException;
import com.itexperts.bank.service.exception.ExistingCardException;

@Service
public class AccountService {

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	CardRepository cardRepository;
	
	@Autowired
	TypeCardRepository typeCardRepository;
	
	public Page<ResponseAccountDto> findAll(Pageable pageable){
		Page<Account> pgAccounts =  accountRepository.findAll(pageable);
		
		Page<ResponseAccountDto> pgDtAccounts = pgAccounts.map(pgAccount -> new ResponseAccountDto(pgAccount));
		
		return pgDtAccounts;
	}
	
	
	public ResponseAccountDto findById(Integer id) {
		Optional<Account> account = accountRepository.findById(id);
		account.orElseThrow(() -> new AccountNotFoundException("Não foi possível encontrar uma conta com o id informado."));
		
		return new ResponseAccountDto(account.get());
	}
	
	
	public ResponseAccountDto createAccount(RequestAccountDto account) {
		//verifica se já existe uma account com o registerId informado
		Optional<Account> accountExis = accountRepository.findByRegisterId(account.getRegisterId());
		if(accountExis.isPresent()) {
			throw new ExistingAccountException("Não foi possível criar uma conta. Já existe uma conta com o 'registerId' informado.");
		}
		
		//verifica se já existe um cartão com o número informado
		account.getCards().forEach(card -> {
			Optional<Card> cardExist = cardRepository.findByNumber(card.getNumber());
			if(cardExist.isPresent()) {
				throw new ExistingCardException("Não foi possível criar um cartão. Já existe um cartão com o 'number' informado.");
			}
			
		});
		
		
		//transaforma o RequestAccountDto em uma entidade de account e salva a nova account
		Account newAccount = account.dtoToEntity();
		accountRepository.save(newAccount);
		
		
		newAccount.getCards().forEach(card -> {
			
			//adiciona o AccountId
			card.setAccountId(newAccount);
			
			//verifica se já existe um TypeCard igual ao TypeCard passado no newCard, com base no nome ignorando caixa alta ou baixa
			Optional<TypeCard> typeCard = typeCardRepository.findByNameIgnoreCase(card.getTypeCard().getName());
			
			if(typeCard.isPresent()) {
				//caso exista adiciona o id do TypeCard existente ao id do TypeCard do newCard
				card.getTypeCard().setId(typeCard.get().getId());
			}else {
				//caso não exista salva esse novo TypeCard na tabela type_card
				typeCardRepository.save(card.getTypeCard());
			}
			
			//salva o card
			cardRepository.save(card);
		});
		
		return new ResponseAccountDto(newAccount);
	}


	public void deleteAccount(Integer id) {
		Optional<Account> account = accountRepository.findById(id);
		account.orElseThrow(() -> new AccountNotFoundException("Não foi possível encontrar uma conta com o id informado."));
		
		if(account.get().getCards().size() != 0) {
			 throw new AccountActiveCardsException("Não foi possível deletar a conta. A conta informada possui cartões ativos.");
		}
		
		accountRepository.deleteById(id);
	}
}
