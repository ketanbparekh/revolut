package com.revolut.bank_transfer.controllers;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.revolut.bank_transfer.api.Account;
import com.revolut.bank_transfer.api.Transaction;
import com.revolut.bank_transfer.dao.AccountDao;

@Path("/api/v1/account")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

    @Inject
    AccountDao accountDao;
    
    final BigDecimal ZERO = new BigDecimal("0.0");
    
    @POST
    public Account createAccount(Account account) throws Exception {
        accountDao.createAccount(account);
        return account;
    }
    
    @GET
    public List<Account> getAllAccounts() {
        List<Account> accounts = accountDao.getAllAccounts();
        return accounts;
    }
    
    @GET
    @Path("details/{accountId}")
    public Account getAccountDetails(@PathParam("accountId") String accountId) throws Exception {
        Account account = null;
        Long id = Long.valueOf(accountId);
        account = accountDao.getAccountDetails(id);
        return account;
    }
    
    @PUT
    @Path("/withdraw/{amount}")
    public Account withdrawFromAccount(Account account, @PathParam("amount") String amount) throws Exception {
        Long id = Long.valueOf(account.getAccountId());
        BigDecimal withdrawlAmt = new BigDecimal(amount);
        account = accountDao.getAccountDetails(id);
        
        if(account.getBalance().subtract(withdrawlAmt).compareTo(ZERO) < 0)
            throw new Exception("Insufficient Balance");
        
        account.setBalance(account.getBalance().subtract(withdrawlAmt));
        accountDao.updateAccount(account);
        return account;
    }
    
    @PUT
    @Path("/deposit/{amount}")
    public Account depositToAccount(Account account, @PathParam("amount") String amount) throws Exception {
        Long id = Long.valueOf(account.getAccountId());
        BigDecimal depositAmt = new BigDecimal(amount);
        account = accountDao.getAccountDetails(id);
        account.setBalance(account.getBalance().add(depositAmt));
        accountDao.updateAccount(account);
        return account;
    }
    
    @POST
    @Path("/transfer")
    public Transaction transferMoney(Transaction transaction) throws Exception {
        transaction = accountDao.transferAmount(transaction);
        return transaction;
    }
    
}
