package com.revolut.bank_transfer.controllers;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.revolut.bank_transfer.api.Transaction;
import com.revolut.bank_transfer.dao.TransactionDao;

@Path("api/v1/transaction")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

    @Inject
    TransactionDao transactionDao;
    
    @GET
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionDao.getAllTransactions();
        return transactions;
    }
    
    @GET
    @Path("/{transactionId}")
    public Transaction getTransactionDetails(@PathParam("transactionId") String transactionId) throws Exception {
        Transaction transaction = transactionDao.getTransactionDetails(transactionId);
        return transaction;
    }
    
    @POST
    @Path("/transfer")
    public Transaction transferMoney(Transaction transaction) throws Exception {
        transaction = transactionDao.transferAmount(transaction);
        return transaction;
        
    }
}
