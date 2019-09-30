package com.revolut.bank_transfer.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.revolut.bank_transfer.api.Account;
import com.revolut.bank_transfer.api.Transaction;

public class AccountDao {

    Map<Long, Account> accountDB = new ConcurrentHashMap<Long, Account>();
    AtomicLong atomicLong = new AtomicLong();
    
    public Account createAccount(Account account) throws Exception {
        account.setAccountId(atomicLong.incrementAndGet());
        accountDB.put(account.getAccountId(), account);
        return account;
    }
    
    public Account getAccountDetails(Long accountId) throws Exception {
        
        if(!accountDB.containsKey(accountId))
            throw new Exception("Account does not exists");
        
        Account account = accountDB.get(accountId);
        return account;
    }
    
    public List<Account> getAllAccounts() {
        
        List<Account> accounts = new ArrayList<Account>();
        for(Map.Entry<Long, Account> entry : accountDB.entrySet())
            accounts.add(entry.getValue());
        return accounts;
    }
    
    public Account updateAccount(Account account) {
        accountDB.put(account.getAccountId(), account);
        return account;
    }
    
    public Transaction transferAmount(Transaction transaction) throws Exception {
        
        Account srcAccount = getAccountDetails(Long.valueOf(transaction.getSenderId()));
        Account destAccount = getAccountDetails(Long.valueOf(transaction.getReceiverId()));
        BigDecimal senderBalance = srcAccount.getBalance();
        
        synchronized (senderBalance) {
            if(senderBalance.compareTo(BigDecimal.ZERO) > 0 && senderBalance.subtract(transaction.getAmount()).compareTo(BigDecimal.ZERO) > 0 ) {
                srcAccount.setBalance(senderBalance.subtract(transaction.getAmount()));
                destAccount.setBalance(destAccount.getBalance().add(transaction.getAmount()));
            } else
                throw new Exception("Insufficient Amout in account to transfer");
            
            updateAccount(srcAccount);
            updateAccount(destAccount);
        }
        return transaction;
    }
}
