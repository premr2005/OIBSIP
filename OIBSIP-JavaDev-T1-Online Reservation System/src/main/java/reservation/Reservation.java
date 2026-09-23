package reservation;

import java.time.LocalDate;

/** One booking. {@code pnr} is null until the booking has been saved. */
public record Reservation(String pnr, String passengerName, int trainNo, String trainName,
                          String classType, LocalDate journeyDate, String source, String destination) {

    public Reservation withPnr(String newPnr) {
        return new Reservation(newPnr, passengerName, trainNo, trainName,
                classType, journeyDate, source, destination);
    }

    /** Multi-line text used in the confirmation and cancellation dialogs. */
    public String toDisplayText() {
        return String.format(
                "PNR             : %s%n"
              + "Passenger       : %s%n"
              + "Train           : %d - %s%n"
              + "Class           : %s%n"
              + "Date of journey : %s%n"
              + "From            : %s%n"
              + "To              : %s",
                pnr, passengerName, trainNo, trainName, classType,
                journeyDate.format(Validation.DATE_FMT), source, destination);
    }
}
