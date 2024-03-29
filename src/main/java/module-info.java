/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 15/10/2021
 */
module julianware.utils.lib {
    requires slf4j.api;
    requires org.jetbrains.annotations;
    requires org.yaml.snakeyaml;

    exports org.julianware.utilslib.string;
    exports org.julianware.utilslib.optional;
    exports org.julianware.utilslib.functional;
    exports org.julianware.utilslib.objects;
    exports org.julianware.utilslib.text.csv;
    exports org.julianware.utilslib.text.csv.model;
    exports org.julianware.utilslib.text.csv.service;
}
