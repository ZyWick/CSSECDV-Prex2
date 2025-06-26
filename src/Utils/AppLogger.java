/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;
import java.util.logging.*;

/**
 *
 * @author Josh
 */
public class AppLogger {

    private static final Logger logger = Logger.getLogger(AppLogger.class.getName());
    
    // Debug level:
    // 0 = OFF, 1 = ERROR, 2 = WARN, 3 = INFO, 4 = DEBUG
    private static int debugLevel = 3; // Default: INFO

    static {
        LogManager.getLogManager().reset(); // Clear default config

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";
            @Override
            public synchronized String format(LogRecord record) {
                return String.format(format,
                        new java.util.Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        record.getMessage()
                );
            }
        });

        logger.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    public static void setDebugLevel(int level) {
        debugLevel = level;
        logInfo("Debug level set to: " + level);
    }

    public static void logDebug(String msg) {
        if (debugLevel >= 4) {
            logger.log(Level.FINE, "[DEBUG] {0}", msg);
        }
    }

    public static void logInfo(String msg) {
        if (debugLevel >= 3) {
            logger.info(msg);
        }
    }

    public static void logWarning(String msg) {
        if (debugLevel >= 2) {
            logger.warning(msg);
        }
    }

    public static void logError(String msg) {
        if (debugLevel >= 1) {
            logger.severe(msg);
        }
    }

    public static void logError(String msg, Throwable ex) {
        if (debugLevel >= 1) {
            logger.log(Level.SEVERE, msg, ex);
        }
    }
}

