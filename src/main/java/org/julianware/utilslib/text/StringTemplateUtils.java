package org.julianware.utilslib.text;

import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 06/09/2022
 */
public class StringTemplateUtils {

    public static List<Keyword> readKeywords(final String template) {
        boolean penReady = false;
        boolean penDown = false;
        StringBuilder stringBuilder = null;
        final List<Keyword> keywords = new ArrayList<>();
        final CharacterIterator characterIterator = new StringCharacterIterator(template);
        while (characterIterator.current() != CharacterIterator.DONE) {
            switch (characterIterator.current()) {
                case '$':
                    if (penDown) {
                        Optional.ofNullable(stringBuilder)
                                .ifPresent(
                                        builder -> builder.append(characterIterator.current())
                                );
                    } else {
                        penReady = true;
                    }
                    break;

                case '{':
                    if (penDown) {
                        Optional.ofNullable(stringBuilder)
                                .ifPresent(
                                        builder -> builder.append(characterIterator.current())
                                );
                    } else if (penReady) {
                        stringBuilder = new StringBuilder();
                        penDown = true;
                    }
                    break;

                case '}':
                    Optional.ofNullable(stringBuilder)
                            .map(StringBuilder::toString)
                            .map(Keyword::new)
                            .ifPresent(keywords::add);
                    stringBuilder = null;
                    penDown = false;
                    penReady = false;
                    break;

                default:
                    if (penDown) {
                        Optional.of(stringBuilder)
                                .ifPresent(
                                        builder -> builder.append(characterIterator.current())
                                );
                    } else {
                        penReady = false;
                    }
                    break;
            }
            characterIterator.next();
        }
        if (penDown) {
            throw new IllegalArgumentException("Output template string is ill-formed, one parenthesis is not closed.");
        }
        return keywords;
    }

    public static String substituteKeywords(
            final String template,
            final Object bean
    ) throws ReflectiveOperationException {
        final List<Keyword> keywords = readKeywords(template);
        String string = template;
        for (final Keyword keyword : keywords) {
            final Method method = bean.getClass().getMethod("get" + keyword.getLabel().substring(0, 1).toUpperCase() + keyword.getLabel().substring(1));
            string = string.replace("${" + keyword.getLabel() + "}", method.invoke(bean).toString());
        }
        return string;
    }

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
