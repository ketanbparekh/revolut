package com.revolut.bank_transfer.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import com.revolut.bank_transfer.api.Account;
import com.revolut.bank_transfer.api.Transaction;

public class TransactionDao {

    @Inject
    AccountDao accountDao;
    
    Map<String, Transaction> transactionDB = new ConcurrentHashMap<String, Transaction>();
    
    public Transaction transferAmount(Transaction transaction) throws Exception {
        
        Account srcAccount = accountDao.getAccountDetails(new Long(transaction.getSenderId()));
        Account destAccount = accountDao.getAccountDetails(new Long(transaction.getReceiverId()));
        
        BigDecimal senderBalance = srcAccount.getBalance();
        synchronized (senderBalance) {
            if(senderBalance.compareTo(BigDecimal.ZERO) > 0 && senderBalance.subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) > 0 ) {
                destAccount.setBalance(destAccount.getBalance().add(transaction.getAmount()));
            } else
                throw new Exception("Insufficient Amout in account to transfer");
            
            accountDao.updateAccount(srcAccount);
            accountDao.updateAccount(destAccount);
            transactionDB.put(UUID.randomUUID().toString(), transaction);
        }
        
        return transaction;
    }
    
    public Transaction getTransactionDetails(String transactionID) throws Exception {

        if(!transactionDB.containsKey(transactionID))
            throw new Exception("TransactionID does not exists");
        
        Transaction transaction = transactionDB.get(transactionID);
        return transaction;
    }
    
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        for(Map.Entry<String, Transaction> entry : transactionDB.entrySet())
            transactions.add(entry.getValue());
        return transactions;
    }
}
