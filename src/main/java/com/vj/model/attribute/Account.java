package com.vj.model.attribute;

public class Account extends StringAttribute {
    public Account(String value) {
        super(value);
    }
    private static Account INSTANCE = null;
    public static Account getAccount() {
        if (INSTANCE == null) {
            synchronized (Account.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Account(System.getProperty("account"));
                }
            }
        }
        return INSTANCE;
    }

}
