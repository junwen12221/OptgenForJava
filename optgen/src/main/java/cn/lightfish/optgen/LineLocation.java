package cn.lightfish.optgen;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LineLocation {
    int line;
    int pos;
    int prev;
}