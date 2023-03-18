package org.xuvigan.atlantacases;

public class Case {
    private String name;
    private int price;
    private String texture;

    public Case(String name, int price, String texture) {
        this.name = name;
        this.price = price;
        this.texture = texture;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public String getTexture() {
        return this.texture;
    }
}