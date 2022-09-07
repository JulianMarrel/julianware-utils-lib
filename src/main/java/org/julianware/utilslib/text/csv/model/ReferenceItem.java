package org.julianware.utilslib.text.csv.model;

import java.util.List;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 17/08/2022
 */
public class ReferenceItem {

    public String label;

    public List<String> fields;

    public Substitution substitute;

    public String tokenize;

    public final String getLabel() {
        return this.label;
    }

    public final List<String> getFields() {
        return this.fields;
    }

    public final String getTokenize() {
        return this.tokenize;
    }

    @Override
    public String toString() {
        return this.label;
    }

    public boolean hasLabelOnly() {
        return
                (fields == null || fields.isEmpty()) &&
                        substitute == null &&
                        tokenize == null;
    }
}
