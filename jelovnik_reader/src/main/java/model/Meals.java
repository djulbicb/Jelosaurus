package model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
public class Meals {
    List<MealItem> breakfast = new ArrayList<>();
    List<MealItem> snack_01 = new ArrayList<>();
    List<MealItem> lunch = new ArrayList<>();
    List<MealItem> snack_02 = new ArrayList<>();
    List<MealItem> dinner = new ArrayList<>();
}
