package com.vj.transform;

import com.vj.transform.attribute.AttributeTransform;
import com.vj.transform.entity.EntityTransform;
import com.vj.transform.identifier.IdentifierTransform;

import java.util.HashMap;
import java.util.Map;

public class Transformers {

    private final EntityTransform unimplementedEntityTransform;
    private final AttributeTransform unimplementedAttributeTransform;
    private final IdentifierTransform unimplementedIdentifierTransform;
    private final Map<Class,EntityTransform> entityTransformMap;
    private final Map<Class,AttributeTransform> attributeTransformMap;
    private final Map<Class,IdentifierTransform> identifierTransformMap;

    public Transformers(EntityTransform unimplementedEntityTransform, AttributeTransform unimplementedAttributeTransform, IdentifierTransform unimplementedIdentifierTransform) {
        this.unimplementedEntityTransform = unimplementedEntityTransform;
        this.unimplementedAttributeTransform = unimplementedAttributeTransform;
        this.unimplementedIdentifierTransform = unimplementedIdentifierTransform;
        entityTransformMap = new HashMap<>();
        attributeTransformMap = new HashMap<>();
        identifierTransformMap = new HashMap<>();
    }

    public void register(Class classTo, EntityTransform transform) {
        entityTransformMap.put(classTo, transform);
    }
    public void register(Class classTo, AttributeTransform transform) {
        attributeTransformMap.put(classTo, transform);
    }
    public void register(Class classTo, IdentifierTransform transform) {
        identifierTransformMap.put(classTo, transform);
    }
    public <T> T entity(Class classTo) {
        return (T) entityTransformMap.getOrDefault(classTo, unimplementedEntityTransform);
    }
    public <T> T attribute(Class classTo) {
        return (T) attributeTransformMap.get(classTo);
    }
    public <T> T idenfifier(Class classTo) {
        return (T) identifierTransformMap.get(classTo);
    }
}
