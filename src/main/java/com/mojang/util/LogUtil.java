package com.mojang.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.mojang.minecraft.Minecraft;

/**
 * Global logging class (to make life easier)
 *
 * @author fragmer
 */
public final class LogUtil {
    private static final String LOG_FILE_NAME = "client.log";
    private static final String LOG_OLD_FILE_NAME = "client.old.log";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Logger logger = Logger.getLogger(LogUtil.class.getName());

    /**
     * Sets up logging to file (%AppData%/.net.classicube.client/client.log)
     */
    static {
        logger.setLevel(Level.ALL);
        CustomFormatter formatter = new CustomFormatter();
        // Disable the default logger
        logger.setUseParentHandlers(false);

        // Set up our console logger
        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);

        // Locate our log files
        File directory = Minecraft.getMinecraftDirectory();
        final File logFile = new File(directory, LOG_FILE_NAME);
        final File logOldFile = new File(directory, LOG_OLD_FILE_NAME);

        // If a logfile already exists, rename it to "client.old.log"
        if (logFile.exists()) {
            if (logOldFile.exists()) {
                logOldFile.delete();
            }
            logFile.renameTo(logOldFile);
        }

        // Set up our logfile handler
        try {
            final FileHandler fileHandler = new FileHandler(logFile.getAbsolutePath());
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (final IOException | SecurityException ex) {
            System.err.println("Error creating log file! " + ex);
        }

        logger.log(Level.INFO, "Log starts on {0}", DATE_FORMAT.format(new Date()));
    }

    private LogUtil() {
    }

    public static void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    public static void logInfo(String message, Throwable exception) {
        logger.log(Level.INFO, message, exception);
    }

    public static void logWarning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void logWarning(String message, Throwable exception) {
        logger.log(Level.WARNING, message, exception);
    }

    public static void logError(String message) {
        logger.log(Level.SEVERE, message);
    }

    public static void logError(String message, Throwable exception) {
        logger.log(Level.SEVERE, message, exception);
    }

    final static class CustomFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            Date eventDate = new Date(record.getMillis());

            // Format:
            // [HH:mm:ss LEVEL] message
            sb.append(TIME_FORMAT.format(eventDate)).append(" [")
                    .append(record.getLevel().getName()).append("] ").append(formatMessage(record))
                    .append(LINE_SEPARATOR);

            Throwable exception = record.getThrown();
            if (exception != null) {
                try {
                    StringWriter sw = new StringWriter();
                    try (PrintWriter pw = new PrintWriter(sw)) {
                        exception.printStackTrace(pw);
                    }
                    sb.append(sw.toString());
                } catch (Exception ex) {
                    // ignore
                }
            }

            return sb.toString();
        }
    }
}
