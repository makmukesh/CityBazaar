package com.vpipl.citybazaar.model;

public class StackHelperBank {
    String code, name;

    public StackHelperBank() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStateName() {
        return name;
    }

    public void setStateName(String name) {
        this.name = name;
    }

    @Override
    public String toString () {
        return name;
    }}