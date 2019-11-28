package cn.lightfish.optgen;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

@Data
@AllArgsConstructor
public class SourceLoc {
    String file;
    int line;
    int pos;


    @Override
    public String toString() {
        Path fileName = Paths.get(file).getFileName();
        return MessageFormat.format("{0}:{1}:{2}",fileName,this.line+1,this.pos+1);
    }
}