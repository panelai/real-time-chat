package swd392.model.dto;

import swd392.enums.StatusNotification;

public class State<T, V> {
    private T value;
    private V error;
    private StatusNotification status;

    public State(StatusNotification status, T value, V error) {
        this.value = value;
        this.error = error;
        this.status = status;
    }

    public T getValue() {
        return value;
    }

    public V getError() {
        return error;
    }

    public StatusNotification getStatus() {
        return status;
    }

    public static <T, V> StateBuilder<T, V> builder() {
        return new StateBuilder<>();
    }
}
