package com.gestaofinanceira.model;

public enum Category {
    COMIDA("Comida"),
    TRANSPORTE("Transporte"),
    CASA("Casa"),
    LAZER("Lazer"),
    SALARIO("Salário");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromString(String text) {
        for (Category c : Category.values()) {
            if (c.displayName.equalsIgnoreCase(text) || c.name().equalsIgnoreCase(text)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
