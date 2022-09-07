package org.julianware.utilslib.text.csv.service;

import org.julianware.utilslib.text.csv.model.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 18/08/2022
 */
public class ReferentialProcessorService {

    private final File outputDir;

    private final Mains mains;

    private ReferentialProcessorService(final String mainPath) throws IOException, URISyntaxException {
        final String input =
                Optional.ofNullable(mainPath)
                        .orElse(System.getProperty("user.dir") + File.separator + "mains.yml");
        try (
                final InputStream inputStream = new FileInputStream(input)
        ) {
            final Constructor constructor = new Constructor(Mains.class);
            final Yaml yaml = new Yaml(constructor);
            this.mains = yaml.load(inputStream);

            final String output =
                    Optional.ofNullable(this.mains.input.dir)
                            .orElse(System.getProperty("user.dir"));
            this.outputDir = Path.of(new URI("file://" + output + File.separator + "output")).toFile();
            if (! (this.outputDir.exists() || this.outputDir.mkdir())) {
                throw new IOException("Could not create output directory '" + this.outputDir.getAbsolutePath() + "'.");
            }
        }
    }

    public static void start(final String mainPath) throws IOException, URISyntaxException {
        final ReferentialProcessorService processor = new ReferentialProcessorService(mainPath);

        final String input =
                Optional.ofNullable(processor.mains.input)
                        .map(Input::getInputFilePath)
                        .orElseThrow(() -> new NoSuchElementException("Input file location is missing."));
        final List<List<String>> records = new ArrayList<>();
        try (
                final InputStream inputStream = new FileInputStream(input);
                final Scanner scanner = new Scanner(inputStream)
        ) {
            while (scanner.hasNextLine()) {
                records.add(
                        ReferentialProcessorService.getRecordFromLine(
                                scanner.nextLine(),
                                "\t"
                        )
                );
            }

            final List<ReferenceItem> references = processor.mains.input.ref;
            final List<String> columnsLabels =
                    references.stream()
                            .map(ReferenceItem::getLabel)
                            .toList();

            final List<Map<ReferenceItem, String>> recordsAsMaps = new ArrayList<>();
            for (final List<String> record : records) {
                final Map<ReferenceItem, String> recordAsMap = new HashMap<>();
                for (int index = 0; index < references.size(); index ++) {
                    recordAsMap.put(
                            references.get(index),
                            record.get(index)
                    );
                }
                recordsAsMaps.add(recordAsMap);
            }

            final List<Map<String, String>> reducedRecords =
                    recordsAsMaps.stream()
                            .map(
                                    recordAsMap -> {
                                        try {
                                            final Map<String, String> reducedRecord = new HashMap<>();
                                            for (final ReferenceItem referenceItem : recordAsMap.keySet()) {
                                                final String stringKey = referenceItem.getLabel();
                                                final String value =
                                                        processor.substitute(
                                                                referenceItem,
                                                                recordAsMap.get(referenceItem)
                                                        );
                                                reducedRecord.put(stringKey, value);
                                            }
                                            return reducedRecord;
                                        } catch (final IOException ioe) {
                                            throw new RuntimeException(ioe);
                                        }
                                    }
                            ).toList();

            for (final Generation generation : processor.mains.output.generate) {
                processor.generate(
                        generation,
                        reducedRecords,
                        columnsLabels
                );
            }
        }
    }

    public final void generate(
            final Generation generation,
            final List<Map<String, String>> reducedRecords,
            final List<String> columnsLabels
    ) throws IOException {
        if ("csv".equals(generation.format)) {
            this.exportAsCSV(
                    reducedRecords,
                    columnsLabels,
                    generation.output
            );
        } else if ("custom".equals(generation.format)) {
            this.exportAsCustom(
                    reducedRecords,
                    generation.template,
                    generation
            );
        }
    }

    public final void exportAsCustom(
            final List<Map<String, String>> reducedRecords,
            final String template,
            final Generation generation
    ) throws IOException {
        final List<Generation.Keyword> keywords = listKeywords(template);
        try (
                final OutputStreamWriter outputStreamWriter =
                        new FileWriter(this.outputDir + File.separator + generation.output)
        ) {
            final AtomicInteger line_idx = new AtomicInteger(1);
            reducedRecords.forEach(
                    reducedRecord -> {
                        try {
                            List<String> outputStrings = List.of(template);
                            for (final Generation.Keyword keyword : keywords) {
                                if ("line_idx".equals(keyword.getLabel())) {
                                    outputStrings =
                                            outputStrings.stream()
                                                    .map(
                                                            outputString -> outputString.replace("${line_idx}", "" + line_idx)
                                                    )
                                                    .collect(Collectors.toList());
                                } else {
                                    final String replacement =
                                            Optional.ofNullable(reducedRecord.get(keyword.getLabel()))
                                                    .map(keyword::getValue)
                                                    .orElseThrow(() -> new RuntimeException("Missing keyword in template: " + keyword + "."));

                                    final List<String> replacements =
                                            Optional.of(generation)
                                                    .map(gen -> computeReplacements(replacement, gen))
                                                    .orElse(List.of(replacement));
                                    final List<String> temp = new ArrayList<>();
                                    for (final String outputString : outputStrings) {
                                        for (final String replacementString : replacements) {
                                            temp.add(outputString.replace("${" + keyword.getPlaceholder() + "}", replacementString));
                                        }
                                    }
                                    outputStrings = temp;
                                }
                            }
                            for (final String outputString : outputStrings) {
                                final AtomicReference<String> atomicOutputString = new AtomicReference<>(outputString);
                                Optional.ofNullable(generation.cleanup)
                                        .ifPresent(
                                                cleanup -> {
                                                    for (final Substitution substitution : generation.cleanup) {
                                                        atomicOutputString.set(atomicOutputString.get().replace(substitution.target, substitution.source));
                                                    }
                                                }
                                        );
                                outputStreamWriter.append(atomicOutputString.get()).append("\n");
                            }
                            line_idx.set(line_idx.get() + 1);
                        } catch (final IOException ioe) {
                            throw new RuntimeException(ioe);
                        }
                    }
            );
        }
    }

    public static List<Generation.Keyword> listKeywords(final String template) {
        if (Optional.ofNullable(template).filter(Predicate.not(String::isBlank)).isEmpty()) {
            return List.of();
        }

        boolean penReady = false;
        boolean penDown = false;
        StringBuilder stringBuilder = null;
        final List<Generation.Keyword> keywords = new ArrayList<>();
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
                            .map(Generation.Keyword::new)
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

    public static List<String> computeReplacements(
            final String replacement,
            final Generation generation
    ) {
        String replaced = replacement;
        for (final Substitution substitution : Optional.ofNullable(generation.substitute).orElse(List.of())) {
            replaced =
                    replaced.replace(
                            substitution.target,
                            substitution.source
                    );
        }

        final String finalReplaced = replaced;
        final List<String> meh = Optional.ofNullable(generation.tokenize)
                .map(
                        delimiter -> {
                            try (
                                    final Scanner stringScanner = new Scanner(finalReplaced)
                            ) {
                                final List<String> replacements = new ArrayList<>();
                                stringScanner.useDelimiter(delimiter);
                                while (stringScanner.hasNext()) {
                                    final String nextToken = stringScanner.next().trim();
                                    if (nextToken.isBlank()) {
                                        continue;
                                    }
                                    replacements.add(nextToken);
                                }
                                return replacements;
                            }
                        }
                )
                .orElse(List.of(finalReplaced));
        return meh;
    }

    public static String substituteKeywords(
            final String stringTemplate,
            final Object bean
    ) throws ReflectiveOperationException {
        String string = stringTemplate;
        for (final Generation.Keyword keyword : listKeywords(stringTemplate)) {
            final Method getter =
                    bean.getClass().getMethod("get" + keyword.getLabel().substring(0, 1).toUpperCase() + keyword.getLabel().substring(1));
            string = string.replace("${" + keyword.getLabel() + "}", getter.invoke(bean).toString());
        }
        return string;
    }

    public final void exportAsCSV(
            final List<Map<String, String>> reducedRecords,
            final List<String> columnsLabels,
            final String output
    ) throws IOException {
        try (
                final OutputStreamWriter outputStreamWriter =
                        new FileWriter(this.outputDir + File.separator + output)
        ) {
            reducedRecords.forEach(
                    reducedRecord -> {
                        try {
                            outputStreamWriter.append(
                                    columnsLabels.stream()
                                            .map(reducedRecord::get)
                                            .collect(Collectors.joining("\t", "", "\n"))
                            );
                        } catch (final IOException ioe) {
                            throw new RuntimeException(ioe);
                        }
                    }
            );
        }
    }

    public final String substitute(
            final ReferenceItem referenceItem,
            final String value
    ) throws IOException {
        if (referenceItem.hasLabelOnly() || value.isBlank()) {
            return value;
        }

        final Substitution substitution = Optional.ofNullable(referenceItem.substitute).orElseThrow(() -> new NoSuchElementException("Substitution instructions are missing for " + referenceItem + "."));
        final String substitutionKey = Optional.ofNullable(substitution.target).map(String::trim).orElseThrow(() -> new NoSuchElementException("Substitution target key is missing for " + referenceItem + "."));
        final String substitutionTargetKey = Optional.ofNullable(substitution.source).map(String::trim).orElseThrow(() -> new NoSuchElementException("Substitution target key is missing for " + referenceItem + "."));

        final List<Properties> dictionary = this.loadDictionary(referenceItem);

        final Optional<String> optionalTokenDelimiter = Optional.ofNullable(referenceItem.tokenize);
        if (optionalTokenDelimiter.isPresent()) {
            final List<String> substitutedTokens = new ArrayList<>();
            final String tokenDelimiter = optionalTokenDelimiter.get();
            try (
                    final Scanner rowScanner = new Scanner(value)
            ) {
                rowScanner.useDelimiter(tokenDelimiter);
                while (rowScanner.hasNext()) {
                    final String nextToken = rowScanner.next().trim();
                    if (nextToken.isBlank()) {
                        continue;
                    }
                    final Properties dictionaryEntry =
                            dictionary.stream()
                                    .filter(
                                            entry -> entry.get(substitutionKey).equals(nextToken)
                                    )
                                    .findFirst()
                                    .orElseThrow(() -> new NoSuchElementException("Could not find any dictionary entry for " + referenceItem + ", '" + substitutionKey + "' = '" + nextToken + "'."));
                    final String substitutedToken = Optional.ofNullable(dictionaryEntry.getProperty(substitutionTargetKey)).orElseThrow(() -> new NoSuchElementException("Could not find substitution key " + substitutionTargetKey + " in dictionary entry for '" + substitutionKey + "' = '" + nextToken + "'."));
                    substitutedTokens.add(substitutedToken);
                }
            }
            return String.join("; ", substitutedTokens);
        } else {
            final String token = value.trim();
            final Properties dictionaryEntry =
                    dictionary.stream()
                            .filter(
                                    entry -> entry.get(substitutionKey).equals(token)
                            )
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException("Could not find any dictionary entry for " + referenceItem + ", '" + substitutionKey + "' = '" + token + "'."));
            return Optional.ofNullable(dictionaryEntry.getProperty(substitutionTargetKey)).orElseThrow(() -> new NoSuchElementException("Could not find substitution key " + substitutionTargetKey + " in dictionary entry for '" + substitutionKey + "' = '" + token + "'."));
        }
    }

    public final List<Properties> loadDictionary(final ReferenceItem referenceItem) throws IOException {
        final List<Properties> dictionary = new ArrayList<>();
        final String dictionaryPath =
                Optional.ofNullable(mains.input.dir)
                        .map(dir -> dir + File.separator)
                        .orElse("")
                        + Optional.ofNullable(referenceItem)
                        .map(ReferenceItem::getLabel)
                        .orElseThrow(() -> new NoSuchElementException("Label header"))
                        + ".csv";
        try (
                final InputStream inputStream = new FileInputStream(dictionaryPath);
                final Scanner scanner = new Scanner(inputStream)
        ) {
            final List<String> fields =
                    Optional.ofNullable(referenceItem.getFields())
                            .orElseThrow(() -> new NoSuchElementException("Fields header"));
            while (scanner.hasNextLine()) {
                final List<String> record =
                        getRecordFromLine(
                                scanner.nextLine(),
                                "\t"
                        );
                if (record.size() != fields.size()) {
                    throw new RuntimeException(referenceItem + " - Record size (" + record.size() + " items) doesn't match described columns size (" + fields.size() + " fields).");
                }
                final Properties dictionaryEntry = new Properties();
                for (int index = 0; index < fields.size(); index ++) {
                    dictionaryEntry.put(fields.get(index), record.get(index));
                }
                dictionary.add(dictionaryEntry);
            }
        }
        return dictionary;
    }

    public static List<String> getRecordFromLine(
            final String line,
            final String delimiter
    ) {
        final List<String> values = new ArrayList<>();
        try (
                final Scanner rowScanner = new Scanner(line)
        ) {
            rowScanner.useDelimiter(delimiter);
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }
}
