import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

class DateFormats {
    public static void main(String[] args) throws Exception {
        System.out.println("main {");
        printDifferently(Instant.now());
        printDifferently(ZonedDateTime.now());
        // printDifferently(LocalDateTime.now());
        printDifferently(OffsetDateTime.now());
        System.out.println("} main");
    }

    private static <T extends TemporalAccessor> void printDifferently(T date) throws Exception {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JavaTimeModule());
        System.out.println("jackson = " + m.writeValueAsString(date));
        System.out.println("toString = " + date);
        System.out.println(
                "iso_ldt = " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(date));
        System.out.println(
                "iso_odt = " + DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault()).format(date));
    }
}
