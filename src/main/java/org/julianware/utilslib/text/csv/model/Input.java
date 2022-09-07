package org.julianware.utilslib.text.csv.model;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 17/08/2022
 */
public class Input {

    public String dir;

    public String file;

    public List<ReferenceItem> ref;

    public String getInputFilePath() {
        return Optional.ofNullable(this.dir).map(dir -> dir + File.separator).orElse("") + this.file;
    }
}
