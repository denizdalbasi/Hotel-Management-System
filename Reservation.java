
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private String username;
    private String city;
    private String hotelName;
    private String startDate;
    private String endDate;
    static final String RESERVATION_FILE_PATH = "C:\\Users\\Ultimate\\Desktop\\java_proje\\reservations.txt";

    public Reservation(String username, String city, String hotelName, String startDate, String endDate) {
        this.username = username;
        this.city = city;
        this.hotelName = hotelName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getUsername() {
        return username;
    }

    public String getCity() {
        return city;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public static List<Reservation> getReservations(String username) {
        List<String> lines = FileHandler.readFile(RESERVATION_FILE_PATH);
        List<Reservation> reservations = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equalsIgnoreCase(username)) {
                reservations.add(new Reservation(parts[0], parts[1], parts[2], parts[3], parts[4]));
            }
        }
        return reservations;
    }

    public static boolean isRoomAvailable(String city, String hotelName, String startDate, String endDate) {
        List<String> lines = FileHandler.readFile(RESERVATION_FILE_PATH);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[1].equalsIgnoreCase(city) && parts[2].equalsIgnoreCase(hotelName)) {
                if (isOverlapping(startDate, endDate, parts[3], parts[4])) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isOverlapping(String startDate1, String endDate1, String startDate2, String endDate2) {
        return startDate1.compareTo(endDate2) <= 0 && endDate1.compareTo(startDate2) >= 0;
    }

    public static void addReservation(Reservation reservation) {
        FileHandler.appendToFile(RESERVATION_FILE_PATH, reservation.getUsername() + "," 
            + reservation.getCity() + "," + reservation.getHotelName() + "," 
            + reservation.getStartDate() + "," + reservation.getEndDate());
    }

    public static void deleteReservation(String username, String city, String hotelName, String startDate, String endDate) {
        List<String> lines = FileHandler.readFile(RESERVATION_FILE_PATH);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            if (!(parts[0].equalsIgnoreCase(username) && parts[1].equalsIgnoreCase(city) 
                && parts[2].equalsIgnoreCase(hotelName) && parts[3].equals(startDate) 
                && parts[4].equals(endDate))) {
                updatedLines.add(line);
            }
        }

        FileHandler.writeFile(RESERVATION_FILE_PATH, updatedLines);
    }
}
