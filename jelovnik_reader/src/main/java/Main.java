import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Comparator.comparing;

public class Main {
    private static String PATH = "C:/Users/User/Documents/Jelovnici";
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static void main(String[] args) throws IOException {
        File[] files = new File(PATH).listFiles();
        List<Day> allDays = new ArrayList<>();

        for (File file : files) {
            WordExtractor extractor = null;
            try
            {
                String fileName = file.getName();


                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                HWPFDocument document = new HWPFDocument(fis);

                extractor = new WordExtractor(document);
                String[] fileData = extractor.getParagraphText();

                List<WeekStrList> weeks = getWeeksAsStr(fileData);
                List<DayStrList> meals = getDayMealsAsStr(weeks);
                DayStrList drinks = getDayDrinksAsStr(weeks);
                System.out.println("---------");
                System.out.println(fileName);
                System.out.println(meals);
                System.out.println(meals.size());

                List<Day> dayList = convertDayFromDayStr(fileName, meals, drinks);
                for (Day day : dayList) {
                    System.out.println(day);
                }

                allDays.addAll(dayList);

            }
            catch (Exception exep)
            {
                exep.printStackTrace();
            }

        }


        Collections.sort(allDays, comparing(Day::getDay));

        FileWriter fileWriter = new FileWriter("data_menu.json");
        fileWriter.write(gson.toJson(allDays));
        fileWriter.close();
    }

    private static DayStrList getDayDrinksAsStr(List<WeekStrList> weeks) {
        DayStrList day = new DayStrList();
        for (WeekStrList week : weeks) {
            for (String line : week.getLines()) {
                if (line.startsWith("Nakon buđenja naredne dve nedelje pijte")) {
                    day.getLines().add(line);
                } else if (line.contains("U toku dana")) {
                    day.getLines().add(line);
                }
            }
        }

        return day;
    }

    private static List<Day> convertDayFromDayStr(String fileName, List<DayStrList> meals, DayStrList drinks) {
        List<Day> days = new ArrayList<>();

        String[] segments = fileName.split(" ");
        int week1 = Integer.parseInt(segments[3]);
        int week2 = Integer.parseInt(segments[6]);

        int startCountDay = (week1 - 1) * 7;

        for (int i = 0; i < meals.size(); i++) {
            int dayCount = startCountDay + i + 1;

            List<MealItem> breakfast = convertMealStrToMeal(meals.get(i).getLines().get(1));
            List<MealItem> snack_01 = convertMealStrToMeal(meals.get(i).getLines().get(2));
            List<MealItem> lunch = convertMealStrToMeal(meals.get(i).getLines().get(3));
            List<MealItem> snack_02 = convertMealStrToMeal(meals.get(i).getLines().get(4));
            List<MealItem> dinner = convertMealStrToMeal(meals.get(i).getLines().get(5));

            List<List<MealItem>> drinksItems = convertDrinkStrToDrink(drinks);

            Meals allMeals = Meals.builder()
                    .breakfast(breakfast)
                    .snack_01(snack_01)
                    .lunch(lunch)
                    .snack_02(snack_02)
                    .dinner(dinner)
                    .build();

            Drinks allDrinks = Drinks.builder()
                    .morning(drinksItems.get(0))
                    .day(drinksItems.get(1))
                    .build();

            Day daysss = Day.builder()
                    .day(dayCount)
                    .week((dayCount - 1) / 7 + 1)
                    .drinks(allDrinks)
                    .meals(allMeals).build();
            days.add(daysss);
        }

        return days;
    }

    private static List<List<MealItem>> convertDrinkStrToDrink(DayStrList drinks) {
        List<List<MealItem>> ddd = new ArrayList<>();
        for (String line : drinks.getLines()) {
            List<MealItem> all = new ArrayList<>();
            ddd.add(all);

            if (line.startsWith("Nakon buđenja naredne dve nedelje pijte 1  supenu kašiku 100% prirodnog soka od aronije razblaženu sa 100ml vode")) {
                MealItem mealItem = MealItem.builder().measure("ml").weight("100").name("Aronija sa mlakom vodom").build();
                all.add(mealItem);
            }

            else if (line.startsWith("Nakon buđenja naredne dve nedelje pijte ½ limun")) {
                MealItem mealItem = MealItem.builder().measure("ml").weight("200").name("Limun sa mlakom vodom").build();
                all.add(mealItem);
            }

            else if (line.startsWith("U toku dana pijete litar limunade ( 1 limun 1 litar vode ), litar čaja od kukuruzne svile  i litar vode.")) {
                MealItem mealItem1 = MealItem.builder().measure("l").weight("1").name("Limunade").build();
                MealItem mealItem2 = MealItem.builder().measure("l").weight("1").name("Čaja od kukuruzne svile").build();
                MealItem mealItem3 = MealItem.builder().measure("l").weight("1").name("Litar vode").build();
                all.add(mealItem1);
                all.add(mealItem2);
                all.add(mealItem3);
            }

            else if (line.startsWith("Nakon buđenja naredne dve nedelje pijte 200ml čaja od đumbira")) {
                MealItem mealItem = MealItem.builder().measure("ml").weight("200").name("Čaj od đumbira").build();
                all.add(mealItem);
            }


            else if (line.contains("dve nedelje pijte 200ml zelenog)") || line.startsWith("Nakon buđenja naredne dve nedelje pijte 200ml zelenog čaja")) {
                MealItem mealItem = MealItem.builder().measure("ml").weight("200").name("Zeleni čaj").build();
                all.add(mealItem);
            }

            else if (line.contains("Nakon buđenja naredne dve nedelje pijte 200ml čaja od svile")) {
                MealItem mealItem = MealItem.builder().measure("ml").weight("200").name("Čaj od svile").build();
                all.add(mealItem);
            }

            else if (line.contains("U toku dana pijete litar limunade ( 1 limun 1 litar vode ), litar čaja od đumbira i litar vode.")) {
                MealItem mealItem1 = MealItem.builder().measure("l").weight("1").name("Limunade").build();
                MealItem mealItem2 = MealItem.builder().measure("l").weight("1").name("Čaj od đumbira").build();
                MealItem mealItem3 = MealItem.builder().measure("l").weight("1").name("Litar vode").build();
                all.add(mealItem1);
                all.add(mealItem2);
                all.add(mealItem3);
            }

            else if (line.contains("U toku dana pijete litar limunade ( 1 limun 1 litar vode ), litar čaja od svile  i litar vode.")) {
                MealItem mealItem1 = MealItem.builder().measure("l").weight("1").name("Limunade").build();
                MealItem mealItem2 = MealItem.builder().measure("l").weight("1").name("Čaj od svile").build();
                MealItem mealItem3 = MealItem.builder().measure("l").weight("1").name("Litar vode").build();
                all.add(mealItem1);
                all.add(mealItem2);
                all.add(mealItem3);
            }

            else if (line.contains("U toku dana pijete litar limunade ( 1 limun 1 litar vode ), litar čaja od đumbira  i litar vode.")) {
                MealItem mealItem1 = MealItem.builder().measure("l").weight("1").name("Limunade").build();
                MealItem mealItem2 = MealItem.builder().measure("l").weight("1").name("Čaj od đumbira").build();
                MealItem mealItem3 = MealItem.builder().measure("l").weight("1").name("Litar vode").build();
                all.add(mealItem1);
                all.add(mealItem2);
                all.add(mealItem3);
            }


            else if (line.contains("U toku dana pijete litar limunade ( 1 limun 1 litar vode ), litar čaja od breze i litar vode.")) {
                MealItem mealItem1 = MealItem.builder().measure("l").weight("1").name("Limunade").build();
                MealItem mealItem2 = MealItem.builder().measure("l").weight("1").name("Čaj od breze").build();
                MealItem mealItem3 = MealItem.builder().measure("l").weight("1").name("Litar vode").build();
                all.add(mealItem1);
                all.add(mealItem2);
                all.add(mealItem3);
            }

            else if (line.equalsIgnoreCase("U toku dana pijete litar limunade ( 1 limun 1 litar vode ), litar čaja od  đumbira i litar vode.")) {
                MealItem mealItem1 = MealItem.builder().measure("l").weight("1").name("Limunade").build();
                MealItem mealItem2 = MealItem.builder().measure("l").weight("1").name("Čaj od đumbira").build();
                MealItem mealItem3 = MealItem.builder().measure("l").weight("1").name("Litar vode").build();
                all.add(mealItem1);
                all.add(mealItem2);
                all.add(mealItem3);
            }

            else if (line.equalsIgnoreCase("U toku dana pijete litar limunade ( 1 limun 1 litar vode ), litar čaja od breze  i litar vode.")) {
                MealItem mealItem1 = MealItem.builder().measure("l").weight("1").name("Limunade").build();
                MealItem mealItem2 = MealItem.builder().measure("l").weight("1").name("Čaj od breze").build();
                MealItem mealItem3 = MealItem.builder().measure("l").weight("1").name("Litar vode").build();
                all.add(mealItem1);
                all.add(mealItem2);
                all.add(mealItem3);
            }


            else  {
                System.out.println("qqq");
            }
        }

        return ddd;
    }

    private static List<MealItem> convertMealStrToMeal(String lines) {
        List<MealItem> meals = new ArrayList<>();
        lines = lines.trim();
        if (lines.equalsIgnoreCase("Meko kuvano jaje+ 8 belanaca + 3-4 kr int hleba")) {
            MealItem meal1 = MealItem.builder()
                    .name("Meko kuvano jaje")
                    .measure("kom")
                    .weight("2")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Integralni hleba")
                    .measure("kom")
                    .weight("3-4")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Meko kuvano jaje+ 8 belanca + salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Meko kuvano jaje")
                    .measure("kom")
                    .weight("2")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Meko kuvana 2 jajeta + 6 belanaca+ salata + 2 kr int hleba")) {
            MealItem meal1 = MealItem.builder()
                    .name("Meko kuvano jaje")
                    .measure("kom")
                    .weight("2")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Integralni hleba")
                    .measure("kom")
                    .weight("2")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("500ml jogurta")) {
            MealItem meal1 = MealItem.builder()
                    .name("Jogurt")
                    .measure("ml")
                    .weight("500")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("kiselo mleko")) {
            MealItem meal1 = MealItem.builder()
                    .name("Kiselo mleko")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("8 mandarina")) {
            MealItem meal1 = MealItem.builder()
                    .name("Mandarina")
                    .measure("kom")
                    .weight("8")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("2 jabuke")) {
            MealItem meal1 = MealItem.builder()
                    .name("Jabuke")
                    .measure("kom")
                    .weight("2")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("1 jabuka")) {
            MealItem meal1 = MealItem.builder()
                    .name("Jabuka")
                    .measure("kom")
                    .weight("2")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("Cezar 200g")) {
            MealItem meal1 = MealItem.builder()
                    .name("Cezar")
                    .measure("g")
                    .weight("200")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("2 banane")) {
            MealItem meal1 = MealItem.builder()
                    .name("Banane")
                    .measure("kom")
                    .weight("2")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("Nemasni sir 300g+  salata+ 3 kr int hleba")) {
            MealItem meal1 = MealItem.builder()
                    .name("Nemasni sir")
                    .measure("g")
                    .weight("300")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Integralni hleb")
                    .measure("kol")
                    .weight("3")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Musaka 450g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Musaka")
                    .measure("g")
                    .weight("450")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Belo meso 250g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Belo meso")
                    .measure("g")
                    .weight("250")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Boranija 450g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Boranija")
                    .measure("g")
                    .weight("450")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Namaz od nemasnog sira + 3 kr int hleba")) {
            MealItem meal1 = MealItem.builder()
                    .name("Namaz od nemasnog sira")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("integralni hleb")
                    .measure("kr")
                    .weight("3")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Cevapi 120g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Cevapi")
                    .measure("g")
                    .weight("120")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Nemasni sir 300g+ salata  + 4 kr int hleba")) {
            MealItem meal1 = MealItem.builder()
                    .name("Nemasni sir")
                    .measure("g")
                    .weight("300")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            MealItem meal3 = MealItem.builder()
                    .name("Salata")
                    .measure("kr")
                    .weight("4")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            meals.add(meal3);
            return meals;
        }

        if (lines.equalsIgnoreCase("Tuna salata 250g")) {
            MealItem meal1 = MealItem.builder()
                    .name("Tuna salata")
                    .measure("g")
                    .weight("250")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("Supa 500ml")) {
            MealItem meal1 = MealItem.builder()
                    .name("Supa")
                    .measure("ml")
                    .weight("500")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("4 integralna  sendvica")) {
            MealItem meal1 = MealItem.builder()
                    .name("Integralna  sendvica")
                    .measure("kom")
                    .weight("4")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("Sopska salata 200g sira")) {
            MealItem meal1 = MealItem.builder()
                    .name("Sopska salata")
                    .measure("g")
                    .weight("200")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("Sopska salata 250g sira")) {
            MealItem meal1 = MealItem.builder()
                    .name("Sopska salata")
                    .measure("g")
                    .weight("250")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("300ml jogurta")) {
            MealItem meal1 = MealItem.builder()
                    .name("Jogurt")
                    .weight("300")
                    .measure("ml")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("6 mandarina")) {
            MealItem meal1 = MealItem.builder()
                    .name("Mandarina")
                    .weight("6")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("4 mandarine")) {
            MealItem meal1 = MealItem.builder()
                    .name("Mandarina")
                    .weight("4")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("2 pomorandze")) {
            MealItem meal1 = MealItem.builder()
                    .name("Pomorandza")
                    .weight("2")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("2 pomoradze")) {
            MealItem meal1 = MealItem.builder()
                    .name("Pomorandza")
                    .weight("2")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("2 kruške")) {
            MealItem meal1 = MealItem.builder()
                    .name("Kruške")
                    .weight("2")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("6 kivija")) {
            MealItem meal1 = MealItem.builder()
                    .name("Kivi")
                    .weight("6")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("8 kivija")) {
            MealItem meal1 = MealItem.builder()
                    .name("Kivi")
                    .weight("8")
                    .build();
            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("4 kr int hleba+ dzem")) {
            MealItem meal1 = MealItem.builder()
                    .name("Interalni hleba")
                    .weight("4")
                    .measure("kr")
                    .build();
            MealItem meal2 = MealItem.builder()
                    .name("Dzem")
                    .build();
            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Junetina 400g+ 200g tikvica+  salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Junetina")
                    .measure("g")
                    .weight("400")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Tikvica")
                    .measure("g")
                    .weight("200")
                    .build();

            MealItem meal3 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            meals.add(meal3);
            return meals;
        }

        if (lines.equalsIgnoreCase("Junetina 420g+ 150g povrća+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Junetina")
                    .measure("g")
                    .weight("400")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Povrca")
                    .measure("g")
                    .weight("150")
                    .build();

            MealItem meal3 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            meals.add(meal3);
            return meals;
        }

        if (lines.equalsIgnoreCase("Junetina 420g+ 150g tikvica+  salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Junetina")
                    .measure("g")
                    .weight("420")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Tikvica")
                    .measure("g")
                    .weight("150")
                    .build();

            MealItem meal3 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            meals.add(meal3);
            return meals;
        }

        if (lines.equalsIgnoreCase("Grašak  450g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Grašak")
                    .measure("g")
                    .weight("450")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Gulas 450g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Gulas")
                    .measure("g")
                    .weight("450")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Piletina 150g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Piletina")
                    .measure("g")
                    .weight("150")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Tuna salata 200g")) {
            MealItem meal1 = MealItem.builder()
                    .name("Tuna salata")
                    .measure("g")
                    .weight("200")
                    .build();

            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("Kuvano jelo  350g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Kuvano jelo")
                    .measure("g")
                    .weight("350")
                    .build();
            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Piletina 350g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Piletina")
                    .measure("g")
                    .weight("350")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Grašak  450g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Piletina")
                    .measure("g")
                    .weight("450")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Cevapi 250g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Cevapi")
                    .measure("g")
                    .weight("250")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Cevapi 200g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Cevapi")
                    .measure("g")
                    .weight("200")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Nemasna riba 450g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Nemasna riba")
                    .measure("g")
                    .weight("450")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Nemasna riba 350g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Nemasna riba")
                    .measure("g")
                    .weight("350")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("8 kašika ovsenih + voćni mix")) {
            MealItem meal1 = MealItem.builder()
                    .name("Kašika ovsenih")
                    .weight("8")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Voćni mix")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("10 kašika ovsenih + mix voćni")) {
            MealItem meal1 = MealItem.builder()
                    .name("Kašika ovsenih")
                    .weight("10")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Voćni mix")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Piletina 450g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Piletina")
                    .measure("g")
                    .weight("450")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("4 mandarine, 1 jabuka")) {
            MealItem meal1 = MealItem.builder()
                    .name("Mandarina")
                    .measure("kom")
                    .weight("4")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Jabuka")
                    .measure("kom")
                    .weight("1")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Palenta 300g+ 500ml jogurta")) {
            MealItem meal1 = MealItem.builder()
                    .name("Palenta")
                    .measure("g")
                    .weight("300")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Jogurt")
                    .measure("ml")
                    .weight("500")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Brusketi 4 kom")) {
            MealItem meal1 = MealItem.builder()
                    .name("Brusketi")
                    .measure("kom")
                    .weight("4")
                    .build();

            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("Palacinke po recepturi + dzem")) {
            MealItem meal1 = MealItem.builder()
                    .name("Palacinke")
                    .measure("g")
                    .weight("300")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Dzem")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Po želji")) {
            MealItem meal1 = MealItem.builder()
                    .name("Po želji")
                    .build();

            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("200g jagoda")) {
            MealItem meal1 = MealItem.builder()
                    .name("Jagode")
                    .measure("g")
                    .weight("200")
                    .build();

            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("200g ananasa") || lines.equalsIgnoreCase("200g ananaasa")) {
            MealItem meal1 = MealItem.builder()
                    .name("Ananasa")
                    .measure("g")
                    .weight("200")
                    .build();

            meals.add(meal1);
            return meals;
        }
        if (lines.equalsIgnoreCase("4 breskve")) {
            MealItem meal1 = MealItem.builder()
                    .name("Breskve")
                    .measure("kom")
                    .weight("4")
                    .build();

            meals.add(meal1);
            return meals;
        }

        if (lines.equalsIgnoreCase("Pastrmka 400g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Pastrmka")
                    .measure("g")
                    .weight("400")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            return meals;
        }

        if (lines.equalsIgnoreCase("Junetina 320g+ 150g tikvica+  salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Junetina")
                    .measure("g")
                    .weight("320")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Tikvice")
                    .measure("g")
                    .weight("150")
                    .build();

            MealItem meal3 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            meals.add(meal3);
            return meals;
        }

        if (lines.equalsIgnoreCase("Kuvano jelo  450g+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Kuvano jelo")
                    .measure("g")
                    .weight("450")
                    .build();
            MealItem meal3 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal3);
            return meals;
    }

        if (lines.equalsIgnoreCase("Junetina 320g+ 150g povrća+ salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Junetina")
                    .measure("g")
                    .weight("320")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Povrće")
                    .measure("g")
                    .weight("150")
                    .build();

            MealItem meal3 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            meals.add(meal3);
            return meals;
        }

        if (lines.equalsIgnoreCase("Junetina 300g+ 200g tikvica+  salata")) {
            MealItem meal1 = MealItem.builder()
                    .name("Junetina")
                    .measure("g")
                    .weight("300")
                    .build();

            MealItem meal2 = MealItem.builder()
                    .name("Tikvice")
                    .measure("g")
                    .weight("200")
                    .build();

            MealItem meal3 = MealItem.builder()
                    .name("Salata")
                    .build();

            meals.add(meal1);
            meals.add(meal2);
            meals.add(meal3);
            return meals;
        }

        return null;
    }

    private static List<DayStrList> getDayMealsAsStr(List<WeekStrList> weeks) {

        List<DayStrList> days = new ArrayList<>();
        DayStrList day = null;

        for (WeekStrList week : weeks) {
            for (String line : week.getLines()) {
                if (Check.isDay(line)) {
                    day = new DayStrList();
                    days.add(day);
                }

                if (day == null) {
                    continue;
                }

                if (line.startsWith("Nakon buđenja naredne")) {
                    break;
                }

                day.getLines().add(line);
            }
        }

        return days;
    }

    private static List<WeekStrList> getWeeksAsStr(String[] fileData) {
        List<WeekStrList> weeks = new ArrayList<>();
        WeekStrList weekStr = null;
        for (int i = 0; i < fileData.length; i++)
        {
            if (fileData[i] != null) {
                String line = fileData[i];
                line = line.replaceAll("\u0007", "");
                line = line.replaceAll("\r\n", "");
                line = line.replaceAll("\\*\\*", "");
                if (line.isEmpty()) {
                    continue;
                }

                if (line.endsWith(" nedelja ") || line.startsWith("19.nedelja ")) {
                    weekStr = new WeekStrList();
                    weeks.add(weekStr);
//                        int weekCount = RomanToInteger.convert(line.split(" ")[0]);
//                        System.out.println(weekCount);
//                        day.setWeek(weekCount);
//                        continue;
                }
                if (weekStr == null) {
                    continue;
                }
                weekStr.getLines().add(line);
            }
        }
        return weeks;
    }
}

