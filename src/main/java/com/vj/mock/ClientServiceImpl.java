package com.vj.mock;

import com.vj.model.attribute.Account;
import com.vj.model.attribute.Client;
import com.vj.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ClientServiceImpl implements ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final Map<String,Client> clientMap = new HashMap<>();
    private final Map<Client,String> clientReverseMap = new HashMap<>();
    private final Map<String,Account> accountMap = new HashMap<>();
    private final Client unregisteredClient = new Client("unregistered-client");
    private final Account noAccount = new Account("no-account");

    @Override
    public void register(String fixCompId, Client client) {
        clientMap.put(fixCompId, client);
        clientReverseMap.put(client, fixCompId);
    }

    @Override
    public void register(String fixCompId, Account account) {
        accountMap.put(fixCompId, account);
    }

    @Override
    public Client lookupClient(String fixCompId) {
        return clientMap.getOrDefault(fixCompId, unregisteredClient);
    }

    @Override
    public Account lookupAccount(String fixCompId) {
        return accountMap.getOrDefault(fixCompId,noAccount);
    }
}
