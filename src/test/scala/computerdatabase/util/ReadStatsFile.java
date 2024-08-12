package computerdatabase.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadStatsFile {
    public static String readStatsFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
