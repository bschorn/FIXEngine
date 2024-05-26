package com.vj.transform;

public class NoTransformationException extends Exception {

    public NoTransformationException(Class<?> classTo, Class<?> classFrom, Object value) {
        super("There is no transformation to " + classTo.getSimpleName() + " for " + classFrom.getSimpleName() + ": " + value);
    }
}
