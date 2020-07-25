package haxlike.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal logger interface
 */
interface EngineLogger {
    void log(String fmt, Object... args);

    /**
     * SLF4J-based logger (TRACE)
     */
    static class Slf4j implements EngineLogger {
        private final Logger log;

        public Slf4j() {
            this.log = LoggerFactory.getLogger(Slf4j.class);
        }

        @Override
        public void log(String fmt, Object... args) {
            log.trace(fmt, args);
        }
    }

    /**
     * No-Op Logger
     */
    static class NoOp implements EngineLogger {

        @Override
        public void log(String fmt, Object... args) {
            // do nothing
        }
    }
}
