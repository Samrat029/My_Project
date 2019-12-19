package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountDoesNotExist;
import com.db.awmd.challenge.exception.InsufficientBalanceException;

public interface SenderRecieverNotification 
{
	public String notifiedToSender(Account senderAccount,Account receiverAccount,BigDecimal amount,String description) throws AccountDoesNotExist,InsufficientBalanceException;
	public void notifiedToReciever(Account account,String description);
}
