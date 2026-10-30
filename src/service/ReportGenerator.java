package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ReportGenerator {

    public static void generate(Path logsFolder, Path reportFile)
            throws IOException {

        long totalDeposits = 0;
        long totalWithdrawals = 0;

        try (DirectoryStream<Path> files =
                     Files.newDirectoryStream(logsFolder)) {

            for (Path file : files) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }

                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");

                        if (parts.length < 3) {
                            continue;
                        }

                        try {
                            long amount = Long.parseLong(parts[2].trim());

                            if (parts[0].trim().equalsIgnoreCase("DEPOSIT")) {
                                totalDeposits += amount;
                            } else if (parts[0].trim()
                                    .equalsIgnoreCase("WITHDRAW")) {
                                totalWithdrawals += amount;
                            }
                        } catch (NumberFormatException ignored) {
                            // Skip malformed transaction amounts.
                        }
                    }
                }

                BasicFileAttributes attributes = Files.readAttributes(
                        file, BasicFileAttributes.class);

                System.out.println("File: " + file.getFileName());
                System.out.println("Size: " + attributes.size() + " bytes");
                System.out.println("Last modified: "
                        + attributes.lastModifiedTime());
            }
        }

        String report = "Total deposits: " + totalDeposits
                + System.lineSeparator()
                + "Total withdrawals: " + totalWithdrawals
                + System.lineSeparator();

        Files.writeString(
                reportFile,
                report,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        System.out.println(report);
    }
}
