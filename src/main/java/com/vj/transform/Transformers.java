package com.vj.transform;

import com.vj.transform.field.FieldTransform;
import com.vj.transform.message.MessageTransform;
import quickfix.Field;
import quickfix.Message;

import java.util.HashMap;
import java.util.Map;

public class Transformers {

    private final MessageTransform unimplementedMessageTransform;
    private final FieldTransform unimplementedFieldTransform;
    private final Map<Class, MessageTransform> entityTransformMap;
    private final Map<Class, FieldTransform> attributeTransformMap;

    public Transformers(MessageTransform unimplementedMessageTransform, FieldTransform unimplementedFieldTransform) {
        this.unimplementedMessageTransform = unimplementedMessageTransform;
        this.unimplementedFieldTransform = unimplementedFieldTransform;
        entityTransformMap = new HashMap<>();
        attributeTransformMap = new HashMap<>();
    }

    public void register(MessageTransform transform) {
        entityTransformMap.put(transform.messageClass(), transform);
    }
    public void register(FieldTransform transform) {
        attributeTransformMap.put(transform.fieldClass(), transform);
    }
    public <T> T message(Class<? extends Message> classTo) {
        return (T) entityTransformMap.getOrDefault(classTo, unimplementedMessageTransform);
    }
    public <T> T field(Class<? extends Field> classTo) {
        return (T) attributeTransformMap.getOrDefault(classTo, unimplementedFieldTransform);
    }
}
