package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.SenderRecieverNotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SenderReceiverNotificationTest
{

	@Autowired
	  private AccountsService accountsService;
	
	@Autowired
	  private SenderRecieverNotificationService senderRecieverNotificationService;
	
	@Test
	  public void notifiedToSender() throws Exception {
		String msg=null;
	    Account acc1 = new Account("Id-111");
	    acc1.setBalance(new BigDecimal(2000));
	    
	    Account acc2 = new Account("Id-112");
	    acc2.setBalance(new BigDecimal(4000));
	    this.accountsService.createAccount(acc1);
	    this.accountsService.createAccount(acc2);
	    
	    msg=senderRecieverNotificationService.notifiedToSender(acc1, acc2, new BigDecimal(2000), "Success");

	    assertEquals(msg,"Success");
	  }
	
	@Test
	  public void notifiedToReceiver() throws Exception {
		String msg=null;
	    Account acc1 = new Account("Id-141");
	    acc1.setBalance(new BigDecimal(2000));
	    
	    Account acc2 = new Account("Id-151");
	    acc2.setBalance(new BigDecimal(4000));
	    this.accountsService.createAccount(acc1);
	    this.accountsService.createAccount(acc2);
	    
	    msg=senderRecieverNotificationService.notifiedToSender(acc1, acc2, new BigDecimal(2000), "Success");
	    assertEquals(msg,"Success");
	    senderRecieverNotificationService.notifiedToReciever(acc2, "Done");

	  }

}
