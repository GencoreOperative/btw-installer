package uk.co.gencoreoperative.btw;

import java.io.IOException;

@FunctionalInterface
public interface CheckedFunction<T> {
    void apply(T t) throws IOException;
}
