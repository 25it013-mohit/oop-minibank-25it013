package service;

import java.nio.file.Path;

public class ReportGeneratorDemo {

    public static void main(String[] args) throws Exception {
        Path logsFolder = Path.of("logs");
        Path reportFile = Path.of("reports", "summary.txt");

        java.nio.file.Files.createDirectories(reportFile.getParent());

        ReportGenerator.generate(logsFolder, reportFile);

        System.out.println("\nReport saved to: " + reportFile);
    }
}
