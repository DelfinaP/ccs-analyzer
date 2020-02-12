import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        LinkedList<String> linkedList = new LinkedList<String>();

        linkedList.add("Prima stringa2");
        linkedList.add("Seconda stringa2");

        List<String> lines = linkedList;
        Path file = Paths.get("the-file-name.txt");
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create new file
        File f = new File("testCreation.txt");

        // creates file in the system
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // tries to delete the newly created file
        boolean bool = f.delete();

        // print
        System.out.println("File deleted: " + bool);
    }
}