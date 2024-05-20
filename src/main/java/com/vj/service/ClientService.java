package com.vj.service;

import com.vj.model.attribute.Client;

public interface ClientService {

    void register(String fixCompId, Client client);
    Client lookup(String fixCompId);
    String lookup(Client client);
}
