package ru.akirakozov.sd.refactoring.dto;

public class ProductDTO {
    private final String name;
    private final int price;

    public ProductDTO(final String name, final int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
