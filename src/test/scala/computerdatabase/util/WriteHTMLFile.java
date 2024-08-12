package computerdatabase.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteHTMLFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteHTMLFile.class.getName());

    /**
     * Writes content to the specified file.
     *
     * @param filePath the file path where the content should be written
     * @param content  the content to write
     * @throws IOException if an I/O error occurs
     */
    public static void writeHTMLFile(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
            LOGGER.info("HTML file written successfully to {}", filePath);
        } catch (IOException e) {
            LOGGER.error("Failed to write HTML file {}", e);
            throw e;
        }
    }

    /**
     * Extracts JSON content from a string.
     *
     * @param content the string content containing JSON
     * @return the extracted JSON string
     */
    public static String extractJson(String content) {
        int startIndex = content.indexOf("{");
        int endIndex = content.lastIndexOf("}") + 1;
        return content.substring(startIndex, endIndex);
    }

    /**
     * Reads the stats file content from the given file path.
     *
     * @param filePath the file path to read the stats file from
     * @return the content of the stats file
     * @throws IOException if an I/O error occurs
     */
    private static String readStatsFile(String filePath) throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            throw new IOException("Stats file not found at " + filePath);
        }

        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Generates an HTML file from the given stats file and writes it to the specified output path.
     *
     * @param filePath            the path to the stats file
     * @param outputHTMLFilePath  the path where the HTML file should be written
     * @throws IOException if an I/O error occurs
     */
    public static void generateHTMLFile(String filePath, String outputHTMLFilePath) throws IOException {
        try {
            String statsContent = readStatsFile(filePath);
            String jsonContent = extractJson(statsContent);
            JSONObject statsJson = new JSONObject(new JSONTokener(jsonContent));
            String htmlContent = GenerateHTML.generateHTMLContent(statsJson);
            writeHTMLFile(outputHTMLFilePath, htmlContent);
            LOGGER.info( "HTML file generated successfully at {}", outputHTMLFilePath);
        } catch (IOException e) {
            LOGGER.info("Failed to generate HTML file {}", e);
            throw e;
        }
    }
}
