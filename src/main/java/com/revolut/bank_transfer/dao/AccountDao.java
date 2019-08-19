package com.revolut.bank_transfer.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.revolut.bank_transfer.api.Account;

public class AccountDao {

    Map<Long, Account> accountDB = new ConcurrentHashMap<Long, Account>();
    AtomicLong atomicLong = new AtomicLong();
    
    public Account createAccount(Account account) throws Exception {
        account.setAccountId(atomicLong.getAndIncrement());
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
}
