package computerdatabase.service;

import computerdatabase.util.FilePicker;
import computerdatabase.util.WriteHTMLFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogFileSender {

    private static String LOGFILEPATH = "target/gatling/";
    private static final String TARGETURL = "http://localhost:8080/api/files/logFile";
    private static final String OUTPUT_HTML_PATH = "src/test/scala/computerdatabase/contents/dashboard.html";
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileSender.class);

    public static void sendLogFile(String logFilePath, String targetUrl) throws IOException {

        Path path = Paths.get(logFilePath);
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)
        ) {
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(String.valueOf(path.getFileName())).append("\"").append(CRLF);
            writer.append("Content-Type: ").append(Files.probeContentType(path)).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(path, output);
            output.flush();
            writer.append(CRLF).flush();
            writer.append("--").append(boundary).append("--").append(CRLF).flush();
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            System.out.println("Log file sent successfully.");
        } else {
            System.err.println("Failed to send log file. Response code: " + responseCode);
        }
    }
    public static void main(String[] args) {
        try {
            String folderName = FilePicker.pickFolder(FilePicker.folderNames());
            String logFilePath = LOGFILEPATH + folderName + "/simulation.log";
            String copy = LOGFILEPATH + folderName;
            WriteHTMLFile.generateHTMLFile(copy + "/js/stats.js", OUTPUT_HTML_PATH);
            sendLogFile(logFilePath, TARGETURL);
        } catch (IOException e) {
            System.err.println("Error sending log file: " + e.getMessage());
        }
    }
}
