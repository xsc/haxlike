package haxlike;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class TestUtil {
    // --- Log
    private static final Level DEFAULT_LEVEL = getLogger().getLevel();

    public static void setTraceLogging() {
        getLogger().setLevel(Level.TRACE);
    }

    public static void resetLogging() {
        getLogger().setLevel(DEFAULT_LEVEL);
    }

    private static Logger getLogger() {
        return ((Logger) LoggerFactory.getLogger("haxlike"));
    }

    // --- Private Constructor
    private TestUtil() {}
}
