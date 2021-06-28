package org.julianware.utilslib.log;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 07/09/2020
 */
public class DebugLoggerUtils
implements AutoCloseable {

    private final Logger logger;

    private final List<DebugItem> debugItems;

    public DebugLoggerUtils(final Logger logger) {
        this.logger = logger;
        this.debugItems = new ArrayList<>();
    }

    public void addDebug(final String formatString, final Object... parameters) {
        this.debugItems.add(new DebugItem(formatString, parameters));
    }

    public void logAll() {
        if (this.logger.isDebugEnabled()) {
            this.debugItems.forEach(debugItem -> debugItem.log(this.logger));
        }
    }

    @Override
    public void close() {
        this.logAll();
        this.debugItems.clear();
    }


    /**
     * @author Julian Marrel <julian.marrel@smile.eu>
     * @created 07/09/2020
     */
    private static class DebugItem {

        private final String formatString;

        private final Object[] parameters;

        private DebugItem(final String formatString, final Object... parameters) {
            this.formatString = formatString;
            this.parameters = parameters;
        }

        private void log(final Logger logger) {
            logger.debug(this.formatString, this.parameters);
        }
    }
}
