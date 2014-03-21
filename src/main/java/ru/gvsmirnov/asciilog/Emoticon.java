package ru.gvsmirnov.asciilog;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface Emoticon {

    Stream<String> getLines();

    default void writeOut(Consumer<String> c) {
        getLines().forEach(c);
    }

    Emoticon TrollFace = new TrollFace();

}
