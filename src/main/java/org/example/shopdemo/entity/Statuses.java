package org.example.shopdemo.entity;

import lombok.Getter;

@Getter
public enum Statuses {
    CREATED(1),
    PROCESSING(2),
    ACCEPT(3),
    DELIVERING(4),
    CANCELLED(5),
    COMPLETED(6);

    private final int id;

    Statuses(int id){
        this.id = id;
    }

    public static Statuses fromId(int id){
        for (Statuses status: values()){
            if (status.getId() == id){
                return status;
            }
        }

        throw new IllegalArgumentException("No status with id " + id);
    }

    public String getDescription() {
        return switch (this) {
            case CREATED -> "Заказ создан";
            case ACCEPT -> "Заказ принят";
            case PROCESSING -> "Заказ собирается";
            case COMPLETED -> "Заказ завершен";
            case CANCELLED -> "Заказ отменен";
            case DELIVERING -> "Заказ доставляется";
            default -> "Неизвестный статус";
        };
    }
}
