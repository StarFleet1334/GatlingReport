package computerdatabase.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class FilePicker {

    public static String[] folderNames() {
        File folder = new File("target/gatling");
        File[] listOfFiles = folder.listFiles(File::isDirectory);

        if (listOfFiles != null) {
            String[] folders = new String[listOfFiles.length];
            for (int i = 0; i < listOfFiles.length; i++) {
                folders[i] = listOfFiles[i].getName();
            }
            return folders;
        } else {
            return new String[0];
        }
    }

    public static String pickFolder(String[] folderNames) {
        Scanner scanner = new Scanner(System.in);
        String[] sortedFolderNames = folderNames.clone();
        Arrays.sort(sortedFolderNames, Collections.reverseOrder());

        System.out.println("All the file names: ");
        for (int i = 0; i < sortedFolderNames.length; i++) {
            System.out.println((i + 1) + ". " + sortedFolderNames[i]);
        }
        System.out.print("Please enter a number between 1 and " + sortedFolderNames.length + ": ");

        int numberChoosen = scanner.nextInt();
        while (numberChoosen < 1 || numberChoosen > sortedFolderNames.length) {
            System.out.print("Invalid number. Please enter a number between 1 and " + sortedFolderNames.length + ": ");
            numberChoosen = scanner.nextInt();
        }

        scanner.close();
        return sortedFolderNames[numberChoosen - 1];
    }

    public static void main(String[] args) {
        FilePicker filePicker = new FilePicker();
        String res = filePicker.pickFolder(filePicker.folderNames());
        System.out.println("You selected: " + res);
    }
}
