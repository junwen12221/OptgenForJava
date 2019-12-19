
package cn.lightfish.optgen;

import lombok.Data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

@Data

public class SourceLoc {
    String file;
    int line;
    int pos;

    public SourceLoc(String file, int line, int pos) {
        this.file = file;
        this.line = line;
        this.pos = pos;
    }

    @Override
    public String toString() {
        Path fileName = Paths.get(file).getFileName();
        return MessageFormat.format("{0}:{1}:{2}",fileName,this.line+1,this.pos+1);
    }
}