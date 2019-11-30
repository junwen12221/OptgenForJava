package cn.lightfish.optgen.testutils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.MessageFormat;
import java.util.List;

@Data
@AllArgsConstructor
public class CmdArg {
    String key;
    List<String> vals;

    @Override
    public String toString() {
        switch (vals.size()){
            case 0:return key;
            case 1:return MessageFormat.format("{0}={1}",key,vals.get(0));
            default:return MessageFormat.format("{0}={1}",String.join(",",key,vals.get(0)));
        }
    }
}