package ch.bytecrowd.kafkazen.service.model;

public class Pair<T, R> {

    private T t;
    private R r;

    public Pair(T t, R r) {
        this.t = t;
        this.r = r;
    }

    public static <T, R>  Pair<T, R> of(T t, R r) {
        return new Pair<>(t, r);
    }

    public T getFirst() {
        return t;
    }
    public R getSecond() {
        return r;
    }
}
