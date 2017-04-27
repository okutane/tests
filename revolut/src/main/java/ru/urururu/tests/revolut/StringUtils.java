package ru.urururu.tests.revolut;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class StringUtils {
    public static Map<Character, Long> countUniqueCharacters(String string) {
        return countUniqueCharacters(string.chars().mapToObj(code -> ((char) code)));
    }

    public static Map<Character, Long> countUniqueCharacters(Stream<Character> stream) {
        return stream.collect(Collectors.groupingBy(Function.identity(), HashMap::new, Collectors.counting()));
    }
}
