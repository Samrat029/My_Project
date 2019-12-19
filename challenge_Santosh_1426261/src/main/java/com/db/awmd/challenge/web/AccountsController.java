package com.db.awmd.challenge.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferMoney;
import com.db.awmd.challenge.exception.AccountDoesNotExist;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.service.SenderRecieverNotificationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;
  
//  @Autowired
//  EmailNotificationService emailNotificationSerivice;
 
  @Autowired
  SenderRecieverNotificationService senderRecieverNotificationService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }
  
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/transferMoney")
  public ResponseEntity<Object> notifyAboutTransfer(@RequestBody TransferMoney transferMoney) {
	  String msg =null;
	  try
	  {
	  msg=senderRecieverNotificationService.notifiedToSender(this.accountsService.getAccount(transferMoney.getAccountFromId()),this.accountsService.getAccount(transferMoney.getAccountToId()),transferMoney.getAmount(),"Acc"+transferMoney.getAccountFromId()+" debited with "+transferMoney.getAmount()+
			  " and credited to "+"Acc"+transferMoney.getAccountToId());
	  }
	  catch(AccountDoesNotExist e)
	  {
		  log.info(e.getMessage()); 
		  return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	  }
	  catch(InsufficientBalanceException e)
	  {
		  log.info(e.getMessage()); 
		  return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	  }
	  if(msg!=null && msg=="Success")
		  senderRecieverNotificationService.notifiedToReciever(this.accountsService.getAccount(transferMoney.getAccountToId()),"Acc"+transferMoney.getAccountToId()+" credited with "+transferMoney.getAmount()+
				  " and debited from "+transferMoney.getAccountFromId());
	  
	  return new ResponseEntity<>(HttpStatus.ACCEPTED);
	  
	  
  }

}
