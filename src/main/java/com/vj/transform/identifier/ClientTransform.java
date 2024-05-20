package com.vj.transform.identifier;

import com.vj.model.attribute.Client;
import quickfix.SessionID;

public class ClientTransform implements IdentifierTransform<SessionID,Client> {

    @Override
    public Client inbound(SessionID sessionID, Object... objects) {
        return null;
    }

    @Override
    public SessionID outbound(Client client, Object... objects) {
        return null;
    }
}
