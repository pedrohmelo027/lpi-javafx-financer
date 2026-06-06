package com.gestaofinanceira.model;

import java.util.Arrays;
import java.util.List;

public class CategoriaFinanceira {
    private int id;
    private String nome;

    public static final CategoriaFinanceira COMIDA = new CategoriaFinanceira(1, "Comida");
    public static final CategoriaFinanceira TRANSPORTE = new CategoriaFinanceira(2, "Transporte");
    public static final CategoriaFinanceira CASA = new CategoriaFinanceira(3, "Casa");
    public static final CategoriaFinanceira LAZER = new CategoriaFinanceira(4, "Lazer");
    public static final CategoriaFinanceira SALARIO = new CategoriaFinanceira(5, "Salário");

    private static final List<CategoriaFinanceira> ALL_CATEGORIES = Arrays.asList(COMIDA, TRANSPORTE, CASA, LAZER, SALARIO);

    public CategoriaFinanceira() {}

    public CategoriaFinanceira(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public static List<CategoriaFinanceira> values() {
        return ALL_CATEGORIES;
    }

    public static CategoriaFinanceira fromString(String text) {
        if (text == null) return null;
        for (CategoriaFinanceira c : ALL_CATEGORIES) {
            if (c.nome.equalsIgnoreCase(text) || c.name().equalsIgnoreCase(text)) {
                return c;
            }
        }
        return new CategoriaFinanceira(0, text);
    }

    public String name() {
        if (nome == null) return "";
        switch (nome) {
            case "Comida": return "COMIDA";
            case "Transporte": return "TRANSPORTE";
            case "Casa": return "CASA";
            case "Lazer": return "LAZER";
            case "Salário": return "SALARIO";
            default: return nome.toUpperCase();
        }
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaFinanceira that = (CategoriaFinanceira) o;
        return nome != null ? nome.equalsIgnoreCase(that.nome) : that.nome == null;
    }

    @Override
    public int hashCode() {
        return nome != null ? nome.toLowerCase().hashCode() : 0;
    }
}
