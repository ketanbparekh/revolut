package com.revolut.bank_transfer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.bank_transfer.api.Account;
import com.revolut.bank_transfer.api.Transaction;
import com.revolut.bank_transfer.api.TransactionType;
import com.revolut.bank_transfer.app.RevolutApplication;
import com.revolut.bank_transfer.app.RevolutConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class AccountControllerTest {

    @ClassRule
    public static final DropwizardAppRule<RevolutConfiguration> appRule = new DropwizardAppRule<RevolutConfiguration>(RevolutApplication.class);
    
    static ObjectMapper objectMapper;
    
    @BeforeClass
    public static void setUp() {
        objectMapper = appRule.getObjectMapper();
        Account accountRequest = new Account();
        accountRequest.setBalance(new BigDecimal("12.86"));
        accountRequest.setUserName("Ketan");
        
        Response response = appRule.client().target(String.format("http://localhost:%d/api/v1/account", appRule.getLocalPort()))
                            .request().post(Entity.entity(accountRequest, MediaType.APPLICATION_JSON_TYPE));

    }
    
//    @Test
//    public void createAccount() throws IOException {
//     
//        Account accountRequest = new Account();
//        accountRequest.setBalance(new BigDecimal("12.86"));
//        accountRequest.setUserName("Ketan");
//        
//        Response response = appRule.client().target(String.format("http://localhost:%d/api/v1/account", appRule.getLocalPort()))
//                            .request().post(Entity.entity(accountRequest, MediaType.APPLICATION_JSON_TYPE));
//
//        assertEquals(response.getStatus(), 200);   
//        Account accountResponse = objectMapper.readValue(response.readEntity(String.class),
//                Account.class);
//        assertEquals(new BigDecimal("12.86"), accountResponse.getBalance());   
//        assertEquals("Ketan",accountResponse.getUserName());
//
//        System.out.println(accountResponse);
//    }
    
    @Test
    public void getAccount() throws IOException {
        
        Response response = appRule.client().target(String.format("http://localhost:%d/api/v1/account/details/7", appRule.getLocalPort()))
                            .request().get();
        assertEquals(500, response.getStatus()); 
        
        Account accountRequest = new Account();
        accountRequest.setBalance(new BigDecimal("15.86"));
        accountRequest.setUserName("Clay");
        response = appRule.client().target(String.format("http://localhost:%d/api/v1/account", appRule.getLocalPort()))
                .request().post(Entity.entity(accountRequest, MediaType.APPLICATION_JSON_TYPE));

        Account accountRequest2 = new Account();
        accountRequest2.setBalance(new BigDecimal("20.03"));
            accountRequest2.setUserName("Zach");
        response = appRule.client().target(String.format("http://localhost:%d/api/v1/account", appRule.getLocalPort()))
                    .request().post(Entity.entity(accountRequest2, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(200,response.getStatus());
      
        Account accountResponse = objectMapper.readValue(response.readEntity(String.class), Account.class);
        Long accountId = accountResponse.getAccountId();

        response = appRule.client()
                .target(String.format("http://localhost:%d/api/v1/account/details/%d", appRule.getLocalPort(), accountId)).request()
                .get();
        accountResponse = objectMapper.readValue(response.readEntity(String.class), Account.class);
        assertEquals(new BigDecimal("20.03"),accountResponse.getBalance());
        assertEquals("Zach",accountResponse.getUserName());

        List<Account> accounts = appRule.client()
                .target(String.format("http://localhost:%d/api/v1/account", appRule.getLocalPort())).request()
                .get(new GenericType<List<Account>>() {});
        assertEquals(3,accounts.size());
        System.out.println(response);
    }
    
    @Test
    public void testTransactions() throws JsonParseException, JsonMappingException, IOException {
    
        Account accountRequest = new Account();
        accountRequest.setUserName("Ketan");
        accountRequest.setAccountId(1L);
        accountRequest.setTransactionType(TransactionType.WITHDRAWL);
        
        Response response = appRule.client().target(String.format("http://localhost:%d/api/v1/account/withdraw/%d", appRule.getLocalPort(), 10))
                .request().put(Entity.entity(accountRequest, MediaType.APPLICATION_JSON_TYPE));
        Account accountResponse = objectMapper.readValue(response.readEntity(String.class), Account.class);
        assertEquals(new BigDecimal("2.86"), accountResponse.getBalance());
        
        accountRequest.setTransactionType(TransactionType.DEPOSIT);
        response = appRule.client().target(String.format("http://localhost:%d/api/v1/account/deposit/%d", appRule.getLocalPort(), 20))
                .request().put(Entity.entity(accountRequest, MediaType.APPLICATION_JSON_TYPE));
        accountResponse = objectMapper.readValue(response.readEntity(String.class), Account.class);
        assertEquals(new BigDecimal("22.86"), accountResponse.getBalance());
    }
    
    @Test
    public void testTransfer() throws JsonParseException, JsonMappingException, IOException {
    
        Account accountRequest1 = new Account();
        accountRequest1.setUserName("Tony");
        accountRequest1.setBalance(new BigDecimal("12.86")); 
        
        Response response = appRule.client().target(String.format("http://localhost:%d/api/v1/account", appRule.getLocalPort()))
                .request().post(Entity.entity(accountRequest1, MediaType.APPLICATION_JSON_TYPE));
        accountRequest1 = objectMapper.readValue(response.readEntity(String.class), Account.class);
        
        Transaction transaction = new Transaction();
        transaction.setSenderId(1L);
        transaction.setReceiverId(accountRequest1.getAccountId());
        transaction.setAmount(new BigDecimal("10"));
        
        response = appRule.client().target(String.format("http://localhost:%d/api/v1/account/transfer", appRule.getLocalPort()))
                .request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(200, response.getStatus()); 

        response = appRule.client()
                .target(String.format("http://localhost:%d/api/v1/account/details/%d", appRule.getLocalPort(), 1)).request()
                .get();
        
        //prev test run makes account 1 with balance of 22.86 so 10 transfer brings it balance to 12.86
        Account accountResponse = objectMapper.readValue(response.readEntity(String.class), Account.class);
        assertEquals(new BigDecimal("12.86"), accountResponse.getBalance());
        
        response = appRule.client()
                .target(String.format("http://localhost:%d/api/v1/account/details/%d", appRule.getLocalPort(), accountRequest1.getAccountId())).request()
                .get();
        accountResponse = objectMapper.readValue(response.readEntity(String.class), Account.class);
        assertEquals(new BigDecimal("22.86"), accountResponse.getBalance());
        
        
        //Insufficient amount exception thrown
        transaction = new Transaction();
        transaction.setSenderId(1L);
        transaction.setReceiverId(accountRequest1.getAccountId());
        transaction.setAmount(new BigDecimal("122.86"));
        response = appRule.client().target(String.format("http://localhost:%d/api/v1/account/transfer", appRule.getLocalPort()))
                .request().post(Entity.entity(transaction, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(500, response.getStatus()); 
        
    }
}
