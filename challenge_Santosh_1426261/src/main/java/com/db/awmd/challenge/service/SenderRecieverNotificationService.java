package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountDoesNotExist;
import com.db.awmd.challenge.exception.InsufficientBalanceException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SenderRecieverNotificationService extends EmailNotificationService implements SenderRecieverNotification 
{

	private final Map<String, String> notificationDetails = new ConcurrentHashMap<>();

	@Override
	public String notifiedToSender(Account senderAccount,Account receiverAccount,BigDecimal amount,String description)throws AccountDoesNotExist,InsufficientBalanceException
	{
		if(senderAccount==null)
			throw new AccountDoesNotExist("AccountFromId Does Not Exist");
		else if(receiverAccount==null)
			throw new AccountDoesNotExist("AccountToId Does Not Exist");
		else if(senderAccount.getBalance().compareTo(amount)==-1)
			throw new InsufficientBalanceException("Account has insufficient Balance");
		notificationDetails.put(senderAccount.getAccountId(), description);
		notifyAboutTransfer(senderAccount, description);
		return "Success";
	}

	@Override
	public void notifiedToReciever(Account account,String description)
	{
		notificationDetails.put(account.getAccountId(), description);
		notifyAboutTransfer(account, description);
	}

	
}
