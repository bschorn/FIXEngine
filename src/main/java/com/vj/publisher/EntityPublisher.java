package com.vj.publisher;


import com.vj.Assembly;
import com.vj.service.Services;

public interface EntityPublisher<T> {

    /**
     * Is this the correct publisher for this entity T?
     */
    default boolean isPublisher(T t) {
        return true;
    }
    void publish(T t);
    default Services services() {
        return Assembly.services();
    }
}
