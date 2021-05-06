package com.minerival.develop.ospermrestrictions;

public enum EventTypes {
    equip("equip"),
    portal("portal"),
    breakwith("breakwith"),
    hit("hit"),
    use("use"),
    consume("consume"),
    resurrect("resurrect"),
    throwing("throwing");

    private final String type;
    EventTypes(String type) {
        this.type = type;
    }
}
