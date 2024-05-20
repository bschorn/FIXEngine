package com.vj.mock;

import com.vj.model.attribute.Client;
import com.vj.service.ClientService;

import java.util.HashMap;
import java.util.Map;

public class ClientServiceImpl implements ClientService {

    private final Map<String,Client> clientMap = new HashMap<>();
    private final Map<Client,String> clientReverseMap = new HashMap<>();
    private final Client unregisteredClient = new Client("unregistered-client");
    private final String unregisteredSession = "unregistered-session";

    @Override
    public void register(String fixCompId, Client client) {
        clientMap.put(fixCompId, client);
        clientReverseMap.put(client, fixCompId);
    }

    @Override
    public Client lookup(String fixCompId) {
        return clientMap.getOrDefault(fixCompId, unregisteredClient);
    }

    @Override
    public String lookup(Client client) {
        return clientReverseMap.getOrDefault(client,unregisteredSession);
    }
}
