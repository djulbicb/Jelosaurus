package model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
public class Day {
    private int day;
    private int week;
    private Meals meals;
    private Drinks drinks;
}
