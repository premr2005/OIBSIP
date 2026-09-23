# Online Reservation System (Java Swing + JDBC + SQLite)

Requires **JDK 17+** and **Maven**. No database server needed - SQLite creates `reservation.db`
in the folder you run from, along with the tables, a default user and a few sample trains.

## Run

    mvn package
    java -jar target/reservation-system.jar

In VS Code: install "Extension Pack for Java", open this folder, and click **Run** above `main()` in `Main.java`.

**Login:** `admin` / `admin123`

**Sample train numbers:** 12951, 12301, 12627, 12009, 12723, 11039, 12859, 12137 (sample data only).

## Files

| File | Role |
|---|---|
| `Main.java` | Starts the app, initialises the DB |
| `Db.java` | All JDBC code (PreparedStatements, PNR generation, PBKDF2 password check) |
| `LoginFrame.java` | Login form, "Access denied", 10s lock after 3 failures |
| `MainFrame.java` | Tabs + logout |
| `ReservationPanel.java` | Booking form, validation, confirmation dialog |
| `CancellationPanel.java` | PNR fetch, details, "Are you sure?" cancel |
| `Validation.java`, `Ui.java`, `Reservation.java` | Helpers and the booking record |

## Manual test checklist

1. Wrong password -> "Access denied"; correct -> main window.
2. Book with empty fields / train `12abc` / date `31-02-2027` / past date -> validation message.
3. Type `12951` in Train number -> train name fills in automatically.
4. Valid booking -> confirmation dialog with a 10-digit PNR.
5. Cancel tab: enter the PNR -> Fetch shows details -> Cancel Booking -> "Are you sure?" -> Yes.
6. Fetch the same PNR again -> "No booking found".

To inspect data: `sqlite3 reservation.db "SELECT * FROM reservations;"` (or the "SQLite Viewer" VS Code extension).
