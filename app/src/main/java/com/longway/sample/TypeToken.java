package com.longway.sample;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

/**
 * Created by longway
 */

public class TypeToken<T> {
    private Class<? super T> mRawType;
    private Type mType;
    private Class<?> mActualType;

    protected TypeToken() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        mType = parameterizedType.getActualTypeArguments()[0];
        mRawType = (Class<? super T>) getRawType(mType);
        mActualType = getType(getClass());
    }

    TypeToken(Type type) {
        mType = type;
        mRawType = (Class<? super T>) getRawType(type);
        mActualType = getActualType(type);
    }

    public static <T> Class<T> getType(Class<T> clz) {
        Type type = clz.getGenericSuperclass();
        return (Class<T>) getRealType(type);
    }

    public static Class<?> getRealType(Type type) {
        Class<?> c = null;
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            if (types != null && types.length > 0) {
                Type t = types[0];
                if (t != null && t instanceof ParameterizedType) {
                    c = getRealType(t);
                } else {
                    c = ((Class<?>) t);
                }
            }
        }
        return c;
    }

    public static TypeToken<?> getTypeToken(Type type) {
        return new TypeToken<Object>(type);
    }

    public static Class<?> getActualType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        return null;
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            // type is a normal class.
            return (Class<?>) type;

        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class.
            // Neal isn't either but suspects some pathological case related
            // to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            checkArgument(rawType instanceof Class);
            return (Class<?>) rawType;

        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();

        } else if (type instanceof TypeVariable) {
            // we could use the variable's bounds, but that won't work if there are multiple.
            // having a raw type that's more general than necessary is okay
            return Object.class;

        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);

        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                    + "GenericArrayType, but <" + type + "> is of type " + className);
        }
    }

    public Class<? super T> getRawType() {
        return mRawType;
    }

    public Type getType() {
        return mType;
    }

    public Class<?> getActualType() {
        return mActualType;
    }

}

