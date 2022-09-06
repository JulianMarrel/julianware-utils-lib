package org.julianware.utilslib.text.csv;

import org.julianware.utilslib.text.csv.service.ReferentialProcessorService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 10/08/2022
 */
public class CSVMain {

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final String mainPath =
                Optional.ofNullable(args)
                        .filter(ar -> ar.length > 0)
                        .map(ar -> ar[0])
                        .orElse("main.yml");
        ReferentialProcessorService.start(mainPath);
    }
}
