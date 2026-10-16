package ro.msg.learning.service.strategy;

public enum OrderStrategyType {

    SINGLE_LOCATION("single-location"),
    MOST_ABUNDANT("most-abundant");

    private final String value;

    OrderStrategyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OrderStrategyType fromPropertyValue(String value) {
        for (OrderStrategyType type : OrderStrategyType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid order strategy: " + value);
    }
}
