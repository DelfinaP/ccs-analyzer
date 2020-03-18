import tool.exceptions.OsNotRecognizedException;
import java.io.*;

import org.json.simple.parser.ParseException;

public class Main {

    public static void main(String[] args) {
        ToolInitializer toolInitializer = new ToolInitializer();
        toolInitializer.run();
    }
}