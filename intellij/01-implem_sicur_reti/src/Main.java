import tool.except.osNotRecognizedException;
import java.io.*;

import org.json.simple.parser.ParseException;

public class Main {

    public static void main(String[] args) throws osNotRecognizedException, ParseException, IOException {
        ToolInitializer toolInitializer = new ToolInitializer();
        toolInitializer.run();
    }
}