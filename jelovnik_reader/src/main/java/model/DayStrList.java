package model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class DayStrList {
    List<String> lines = new ArrayList<>();
}
