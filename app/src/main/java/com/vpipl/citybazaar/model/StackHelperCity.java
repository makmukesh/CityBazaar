package com.vpipl.citybazaar.model;

public class StackHelperCity {
    String code, name;

    public StackHelperCity() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString () {
        return name;
    }}