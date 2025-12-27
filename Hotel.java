import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private String city;
    private String name;
    private String view;
    private int capacity;
    private String type;
    static final String CITY_FILE_PATH = "C:\\Users\\Ultimate\\Desktop\\java_proje\\sehirler.txt";
    static final String INFORMATION_FILE_PATH = "C:\\Users\\Ultimate\\Desktop\\java_proje\\OTEL_BILGI.txt";

    public Hotel(String city, String name, String view, int capacity, String type) {
        this.city = city;
        this.name = name;
        this.view = view;
        this.capacity = capacity;
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getView() {
        return view;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getType() {
        return type;
    }

    public static List<String> getCities() {
        List<String> lines = FileHandler.readFile(CITY_FILE_PATH);
        List<String> cities = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",", 2);
            if (!cities.contains(parts[0])) {
                cities.add(parts[0]);
            }
        }
        return cities;
    }

    public static List<String> getHotelsInCity(String city) {
        List<String> lines = FileHandler.readFile(CITY_FILE_PATH);
        List<String> hotels = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(",", 2);
            if (parts[0].equalsIgnoreCase(city)) {
                hotels.add(parts[1]);
            }
        }
        return hotels;
    }

    public static void addHotel(Hotel hotel) {
        FileHandler.appendToFile(CITY_FILE_PATH, hotel.getCity() + "," + hotel.getName());
        FileHandler.appendToFile(INFORMATION_FILE_PATH, hotel.getCity() + "," + hotel.getName() + "," 
            + hotel.getView() + "," + hotel.getCapacity() + "," + hotel.getType());
    }

    public static void deleteHotel(String city, String name) {
        List<String> cityLines = FileHandler.readFile(CITY_FILE_PATH);
        List<String> infoLines = FileHandler.readFile(INFORMATION_FILE_PATH);
        List<String> updatedCityLines = new ArrayList<>();
        List<String> updatedInfoLines = new ArrayList<>();

        for (String line : cityLines) {
            String[] parts = line.split(",", 2);
            if (!(parts[0].equalsIgnoreCase(city) && parts[1].equalsIgnoreCase(name))) {
                updatedCityLines.add(line);
            }
        }

        for (String line : infoLines) {
            String[] parts = line.split(",", 2);
            if (!(parts[0].equalsIgnoreCase(city) && parts[1].equalsIgnoreCase(name))) {
                updatedInfoLines.add(line);
            }
        }

        FileHandler.writeFile(CITY_FILE_PATH, updatedCityLines);
        FileHandler.writeFile(INFORMATION_FILE_PATH, updatedInfoLines);
    }

    public static String getHotelInfo(String city, String name) {
        List<String> lines = FileHandler.readFile(INFORMATION_FILE_PATH);
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts[0].equalsIgnoreCase(city) && parts[1].equalsIgnoreCase(name)) {
                return String.format("Hotel: %s\nCity: %s\nView: %s\nCapacity: %d\nType: %s",
                    parts[1], parts[0], parts[2], Integer.parseInt(parts[3]), parts[4]);
            }
        }
        return "Hotel information not found.";
    }
}
