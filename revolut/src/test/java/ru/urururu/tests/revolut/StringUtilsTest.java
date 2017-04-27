package ru.urururu.tests.revolut;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class StringUtilsTest {
    @Test
    public void countUniqueCharacters() throws Exception {
        // todo rewrite with assertj

        Assert.assertEquals(Collections.emptyMap(), StringUtils.countUniqueCharacters(""));

        Assert.assertEquals(Collections.singletonMap('a', 1L), StringUtils.countUniqueCharacters("a"));

        testCountCharacters("abba ", new HashMap<Character, Long>() {{
            put('a', 2L);
            put('b', 2L);
            put(' ', 1L);
        }});
    }

    private void testCountCharacters(String string, HashMap<Character, Long> expected) {
        Assert.assertEquals(string, expected, StringUtils.countUniqueCharacters(string));
    }

    @Test
    public void testCountLotsOfCharacters() {
        long size = (long)3000000;

        Iterable<Character> lotsOfCharacters = new Iterable<Character>() {
            @Override
            public Iterator<Character> iterator() {
                return new Iterator<Character>() {
                    long position = 0;

                    @Override
                    public boolean hasNext() {
                        return position < size;
                    }

                    @Override
                    public Character next() {
                        position++;
                        return 'a';
                    }
                };
            }
        };

        Assert.assertEquals(
                Collections.singletonMap('a', size),
                StringUtils.countUniqueCharacters(StreamSupport.stream(lotsOfCharacters.spliterator(),false))
        );
    }
}
