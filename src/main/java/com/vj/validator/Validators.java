package com.vj.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validators {

    private final Map<Class<?>,List<MessageValidator>> validators;

    public Validators() {
         validators = new HashMap<>();
    }

    public void register(Class<?> messageClass, MessageValidator messageValidator) {
        List<MessageValidator> list = validators.getOrDefault(messageClass, new ArrayList<>());
        list.add(messageValidator);
        validators.put(messageClass, list);
    }

    public <T> List<T> get(Class<?> messageClass) {
        return (List<T>) validators.getOrDefault(messageClass, new ArrayList<>());
    }
}
