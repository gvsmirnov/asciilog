package ru.gvsmirnov.emotional;

import java.util.stream.Stream;

public class TrollFace implements Emoticon {

    private static final String[] LINES = {
            "TROLL",
            "FACE"
    };

    @Override
    public Stream<String> getLines() {
        return Stream.of(LINES);
    }
}
