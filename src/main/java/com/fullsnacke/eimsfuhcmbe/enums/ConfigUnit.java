package com.fullsnacke.eimsfuhcmbe.enums;

public enum ConfigUnit {
    VND("vnd"),
    SLOT("slot");

    private final String value;

    ConfigUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}