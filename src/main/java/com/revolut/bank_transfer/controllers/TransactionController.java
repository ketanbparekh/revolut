package com.revolut.bank_transfer.controllers;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.revolut.bank_transfer.api.Transaction;

@Path("api/v1/transaction")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

    @POST
    public Transaction createTransaction(Transaction transaction) {
        return transaction;
    }
    
    @GET
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = null;
        return transactions;
    }
    
    @GET
    @Path("/{transactionId}")
    public Transaction getTransactionDetails(@PathParam("transactionId") String transactionId) {
            Transaction transaction = null;
        return transaction;
    }
}
