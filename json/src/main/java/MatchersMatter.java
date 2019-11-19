import java.time.LocalDateTime;

import org.hamcrest.Matcher;
import org.hamcrest.core.SubstringMatcher;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MatchersMatter {
    private static class Response {
        String getActivationDate() {
            return "2019-10-18"; // for example
        }
    }

    public static void main(String[] args) {
        LocalDateTime activationDate = LocalDateTime.of(2019, 10, 17, 13, 30);
        Response response = new Response();

        assertAll(
                // () -> assertThat("activationDate",
                // String.valueOf(activationDate).startsWith(response.getActivationDate()),
                // is(true)),
                () -> assertThat("activationDate", String.valueOf(activationDate),
                        startsWith(response.getActivationDate())),
                () -> assertThat("activationDate", response.getActivationDate(),
                        isPrefixFor(String.valueOf(activationDate))));
    }

    static Matcher<String> isPrefixFor(String longer) {
        return new SubstringMatcher(longer) {
            @Override
            protected String relationship() {
                return "prefixing";
            }

            @Override
            protected boolean evalSubstringOf(String s) {
                return longer.startsWith(s);
            }
        };
    }
}
