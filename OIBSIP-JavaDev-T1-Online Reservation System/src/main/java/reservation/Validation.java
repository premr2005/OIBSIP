package reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Small, dependency-free input checks shared by the forms. */
public final class Validation {

    public static final String DATE_HINT = "DD-MM-YYYY";
    /** STRICT so that 31-02-2026 is rejected instead of silently adjusted. */
    public static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private Validation() {}

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNumeric(String s) {
        return s != null && s.matches("\\d{1,6}");
    }

    public static boolean isValidName(String s) {
        return s != null && s.trim().matches("[A-Za-z][A-Za-z .'-]{0,49}");
    }

    public static boolean isValidPnr(String s) {
        return s != null && s.matches("\\d{10}");
    }

    /** Returns the parsed date, or null if the text is not a real date in DD-MM-YYYY form. */
    public static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
