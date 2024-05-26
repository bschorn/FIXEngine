package com.vj.service;

import com.vj.model.attribute.Account;
import com.vj.model.attribute.Client;

public interface ClientService {

    void register(String fixCompId, Client client);
    void register(String fixCompId, Account account);
    Client lookupClient(String fixCompId);
    Account lookupAccount(String fixCompId);
}
