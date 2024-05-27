package com.vj.service;

import com.vj.model.attribute.Account;
import com.vj.model.attribute.Client;

public interface ClientService {

    void register(String fixCompId, Client client);
    void register(String fixCompId, Account account);
    Client lookupClient(String fixCompId) throws NoClientFoundException;
    Account lookupAccount(String fixCompId) throws NoAccountFoundException;

    class NoClientFoundException extends Exception {
        NoClientFoundException(String message) {
            super(message);
        }
    }

    class NoAccountFoundException extends Exception {
        NoAccountFoundException(String message) {
            super(message);
        }
    }
}
