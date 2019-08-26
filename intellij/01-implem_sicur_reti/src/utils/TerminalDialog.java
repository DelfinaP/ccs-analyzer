package utils;

import java.io.*;

public class TerminalDialog  {
    public static void method1() throws IOException {
        String line;

        Process process = Runtime.getRuntime().exec("/bin/bash");
        OutputStream stdin = process.getOutputStream ();
        InputStream stderr = process.getErrorStream ();
        InputStream stdout = process.getInputStream ();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

        String input = "cd ~";
        input += "\n";
        writer.write(input);
        writer.flush();

        input = "ls -l";
        input += "\n";
        writer.write(input);
        writer.flush();

        while ((line = reader.readLine()) != null) {
            System.out.println("Stdout: " + line);
        }
    }


}
