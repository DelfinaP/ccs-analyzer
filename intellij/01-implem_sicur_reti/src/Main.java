import utils.except.osNotRecognizedException;

import java.io.*;
import org.json.simple.parser.ParseException;

public class Main {

    public static void main(String[] args) throws IOException, osNotRecognizedException, ParseException {

        Tool tool = new Tool();
        tool.run();
    }
}