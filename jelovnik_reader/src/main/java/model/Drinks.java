package model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
public class Drinks {
    List<MealItem> morning = new ArrayList<>();
    List<MealItem> day = new ArrayList<>();
}
