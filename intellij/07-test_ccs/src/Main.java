import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        String filePath = "C:\\Users\\Piera\\Documents\\GitHub\\uni-sicur-reti\\intellij\\07-test_ccs\\src\\text.txt";
        String originalString = "aaa.fff.ccc";
        String modifiedString = "aaa.ggg.ccc";

        replaceLineInFile(filePath, originalString, modifiedString);
    }

    public static void replaceLineInFile(String filePath, String originalString, String modifiedString) {
        try {
            // Input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(filePath));
            StringBuffer inputBuffer = new StringBuffer();
            String line;
            LinkedList<String> linesList = new LinkedList<String>();

            while ((line = file.readLine()) != null) {
                linesList.add(line);
            }
            file.close();

            // Search 'originalString'
            int i = 0;
            while (i < linesList.size() && !linesList.get(i).equals(originalString)) {
                i++;
            }

            if (i < linesList.size()) {
                // Substitute 'originalString' with 'modifiedString'
                linesList.set(i, modifiedString);

                // Add text lines to inputBuffer
                for (int j = 0; j < linesList.size(); j++) {
                    inputBuffer.append(linesList.get(j));
                    inputBuffer.append('\n');
                }

                // Overwrite the file substituting 'originalString' with 'modifiedString'
                FileOutputStream fileOut = new FileOutputStream(filePath);
                fileOut.write(inputBuffer.toString().getBytes());
                fileOut.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}