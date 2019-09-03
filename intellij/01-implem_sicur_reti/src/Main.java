import utils.except.osNotRecognizedException;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, osNotRecognizedException {
        boolean isEsecuzioneDebug = true;

        Tool tool = new Tool();
        tool.run(isEsecuzioneDebug);
    }
}