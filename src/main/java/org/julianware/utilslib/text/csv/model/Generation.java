package org.julianware.utilslib.text.csv.model;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 17/08/2022
 */
public class Generation {

    public String format;

    public String output;

    public String template;

    public List<Substitution> substitute;

    public String tokenize;

    public List<Substitution> cleanup;


    /**
     * @author Julian Marrel <julian.marrel@smile.eu>
     * @created 17/08/2022
     */
    public static final class Keyword {

        private final String label;

        private final String defaultValue;

        public Keyword(final String value) {
            final Scanner scanner = new Scanner(value);
            scanner.useDelimiter(":");
            this.label = scanner.next();
            if (scanner.hasNext()) {
                this.defaultValue = scanner.next();
            } else {
                this.defaultValue = null;
            }
        }

        public String getLabel() {
            return this.label;
        }

        public String getValue(final String value) {
            if (value == null || value.isBlank()) {
                if (this.defaultValue == null) {
                    return value;
                }
                return "NULL".equals(this.defaultValue) ? "NULL" : this.defaultValue;
            }
            return value;
        }

        public String getPlaceholder() {
            return this.label + Optional.ofNullable(this.defaultValue).map(val -> ":" + val).orElse("");
        }

        @Override
        public String toString() {
            return this.label;
        }
    }
}
