package com.uranusdb.server;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ID="id";
    public static final String LABEL="label";
    public static final String LABEL1="label1";
    public static final String LABEL2="label2";
    public static final String KEY="key";
    public static final String TYPE="type";
    public static final String FROM="from";
    public static final String TO="to";
    public static final String NUMBER="number";

    static final String GET="GET";
    static final String POST="POST";
    static final String DELETE="DELETE";
    static final String PUT="PUT";
    static final String PATH_REL_TYPES="/relationship_types";
    static final String PATH_REL_TYPES_COUNT="/relationship_types/count";
    static final String PATH_REL_TYPE_COUNT="/relationship_types/{type}/count";
    static final String PATH_NODE="/node/{label}/{id}";
    static final String PATH_NODE_PROPERTIES="/node/{label}/{id}/properties";
    static final String PATH_NODE_PROPERTY="/node/{label}/{id}/property/{key}";
    static final String PATH_REL="/relationship/{type}/{label1}/{from}/{label2}/{to}";
    static final String PATH_REL_PROPERTIES ="/relationship/{type}/{label1}/{from}/{label2}/{to}/properties";
    static final String PATH_REL_PROPERTY="/relationship/{type}/{label1}/{from}/{label2}/{to}/property/{key}";
    static final String PATH_ADD_REL="/relationship/{type}/{label1}/{from}/{label2}/{to}/{number}";
    static final String PATH_ADD_REL_PROPERTIES="/relationship/{type}/{label1}/{from}/{label2}/{to}/{number}/properties";
    static final String PATH_ADD_REL_PROPERTY="/relationship/{type}/{label1}/{from}/{label2}/{to}/{number}/property/{key}";
    static final String PATH_RELATED ="/related/{label1}/{from}/{label2}/{to}";
    static final String PATH_RELATED_TYPE ="/related/{type}/{label1}/{from}/{label2}/{to}";
}