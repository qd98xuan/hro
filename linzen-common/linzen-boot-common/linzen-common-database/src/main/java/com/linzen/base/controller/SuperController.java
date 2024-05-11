package com.linzen.base.controller;

import com.linzen.base.service.SuperService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Data
public abstract class SuperController<S extends SuperService<Entity>, Entity> {


    @Autowired
    protected S baseService;

    Class<Entity> entityClass = null;

    public Class<Entity> getEntityClass() {
        if (entityClass == null) {
            Type type = this.getClass().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            this.entityClass = (Class<Entity>) types[1];
        }
        return this.entityClass;
    }
}
