package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void createAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    Account account = accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }

  @Test
  public void createDuplicateAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNegativeBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountEmptyAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void getAccount() throws Exception {
    String uniqueAccountId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
    this.accountsService.createAccount(account);
    this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId))
      .andExpect(status().isOk())
      .andExpect(
        content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
  }
  
  @Test
  public void notifyAboutTransfer() throws Exception {
	 
	Account acc1 = new Account("Id-123", new BigDecimal("1000"));
    this.accountsService.createAccount(acc1);
	Account acc2 = new Account("Id-113", new BigDecimal("4000"));
    this.accountsService.createAccount(acc2);
    this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-113\",\"amount\":1000}")).andExpect(status().isAccepted());

    assertThat(acc1.getAccountId()).isEqualTo("Id-123");
    assertThat(acc2.getAccountId()).isEqualTo("Id-113");
    assertThat(acc1.getBalance()).isEqualByComparingTo("1000");
  }
  
 
  @Test
  public void notifyAboutTransferNoAccountFromId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"\",\"accountToId\":\"Id-113\",\"amount\":1000}")).andExpect(status().isBadRequest());
  }
  
  @Test
  public void notifyAboutTransferNoAccountToId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"\",\"amount\":1000}")).andExpect(status().isBadRequest());
  }
  
  @Test
  public void notifyAboutTransferNoAmount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-113\",\"amount\":}")).andExpect(status().isBadRequest());
  }

  @Test
  public void notifyAboutTransferAccountFromIdDoesNotExist() throws Exception {
	 
	Account acc1 = new Account("Id-123", new BigDecimal("1000"));
    this.accountsService.createAccount(acc1);
	Account acc2 = new Account("Id-113", new BigDecimal("4000"));
    this.accountsService.createAccount(acc2);
    this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-111\",\"accountToId\":\"Id-113\",\"amount\":1000}")).andExpect(status().isBadRequest());

  }
  
  @Test
  public void notifyAboutTransferAccountToIdDoesNotExist() throws Exception {
	 
	Account acc1 = new Account("Id-123", new BigDecimal("1000"));
    this.accountsService.createAccount(acc1);
	Account acc2 = new Account("Id-111", new BigDecimal("4000"));
    this.accountsService.createAccount(acc2);
    this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-113\",\"amount\":1000}")).andExpect(status().isBadRequest());

  }
  
  @Test
  public void notifyAboutTransferNotSufficientBalance() throws Exception {
	 
	Account acc1 = new Account("Id-123", new BigDecimal("1000"));
    this.accountsService.createAccount(acc1);
	Account acc2 = new Account("Id-111", new BigDecimal("4000"));
    this.accountsService.createAccount(acc2);
    this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-113\",\"amount\":2000}")).andExpect(status().isBadRequest());

  }
}
