package cn.lightfish.optgen.testutils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestData {
    String pos;
    String cmd;
    List<CmdArg> cmdArgs = new ArrayList<>();

    String input;
    String expected;
}