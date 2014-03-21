package ru.gvsmirnov.emotional;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class EmoticonTest {

    @Test
    public void testWithDirectSystemOut() {
        TEST_EMOTICON.writeOut(System.out::println);

        validateOutput(Matcher.identity());
    }

    @Test
    public void testWithModifiedSystemOut() {
        final Matcher matcher = s -> "> " + s;

        TEST_EMOTICON.writeOut(s -> System.out.println(matcher.apply(s)));

        validateOutput(matcher);
    }

    @Test
    public void testWithLog4j() {
        org.apache.log4j.Logger log = getLog4jLogger();

        TEST_EMOTICON.writeOut(log::info);

        validateOutput(s -> "INFO " + getClass().getCanonicalName() + " " + s);
    }

    private static final String[] LINES = {
            "The sense of the Green",
            "Spikes grown all over your poor back",
            "Take a tin and go"
    };

    private static final Emoticon TEST_EMOTICON = () -> Stream.of(LINES);

    private static final ByteArrayOutputStream OUTPUT = new ByteArrayOutputStream();

    @BeforeClass
    public static void interceptSystemOut() {
        System.setOut(new PrintStream(OUTPUT));
    }

    @Before
    public void init() {
        OUTPUT.reset();
    }

    @FunctionalInterface
    private interface Matcher extends Function<String, String> {

        String constructExpected(String sourceLine);

        default String apply(String s) {
            return constructExpected(s);
        }

        public static Matcher identity() {
            return s -> s;
        }
    }

    private  void validateOutput(Matcher matcher) {
        Iterator<String> sourceLines = Arrays.asList(LINES).iterator();
        getWrittenLines().forEach(actualLine -> {
            if(!sourceLines.hasNext()) {
                Assert.fail("Extra line(s) in output: " + actualLine);
            } else {
                Assert.assertEquals(matcher.constructExpected(sourceLines.next()), actualLine);
            }
        });

        if(sourceLines.hasNext()) {
            Assert.fail("Some resulting lines have not been matched: " + sourceLines.next());
        }
    }

    private Stream<String> getWrittenLines() {
        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(OUTPUT.toByteArray())
                )
        ).lines();
    }

    private org.apache.log4j.Logger getLog4jLogger() {
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EmoticonTest.class);

        ConsoleAppender console = new ConsoleAppender();
        console.setLayout(new PatternLayout("%p %c %m%n"));
        console.activateOptions();

        log.removeAllAppenders();
        log.addAppender(console);

        return log;
    }

}