import java.util.Arrays;
import java.util.List;

public class Check {
    private static List<String> allDays = Arrays.asList("Ponedeljak", "Utorak", "Sreda", "Četvrtak", "Petak", "Subota", "Nedelja");

    public static boolean isDay(String day) {
        if (day == null || day.isEmpty()) {
            return false;
        }
        if (allDays.contains(day.trim())) {
            return true;
        }
        return false;
    }
}
