package fr.tsadeo.app.dsntotree.util;

import java.io.InputStream;

import fr.tsadeo.app.dsntotree.model.Dsn;

public class JsonUtils implements IJsonConstants, IConstants {

    public InputStream getJsonForDsnAsStream(Dsn dsn) {
    	String path = this.getPathForPhase(dsn);
    	String filename = this.getFilenameForNature(dsn);
        return path == null || filename == null ?null:this.getJsonFileAsStream(path, filename);
    }

    public InputStream getJsonEnteteForDsnAsStream(Dsn dsn) {

    	String path = this.getPathForPhase(dsn);
        return path == null? null:this.getJsonFileAsStream(path, JSON_FILE_DSN_ENTETE);
    }

    private InputStream getJsonFileAsStream(String path, String filename) {

        if (filename == null || path == null) {
            throw new RuntimeException("Impossible de localiser le fichier json !");
        }
        String completePath = path.concat(filename);

        return this.getClass().getClassLoader().getResourceAsStream(completePath);

        // URL url = this.getClass().getClassLoader().getResource(completePath);
        // File jsonFile = (url == null) ? null : new File(url.getFile());
        // System.out.println(jsonFile == null ? "aucun fichier json trouvé!" :
        // jsonFile.getAbsolutePath());
        // return jsonFile;

    }

    private String getFilenameForNature(Dsn dsn) {

        if (this.isDsnMensuelle(dsn)) {
            return JSON_FILE_DSN_MENSUELLE;
        } else if (this.isDsnSignalArretTravail(dsn)) {
            return JSON_FILE_DSN_SIGNAL_ARRET_TRAVAIL;
        } else if (this.isDsnSignalFinContrat(dsn)) {
            return JSON_FILE_DSN_SIGNAL_FIN_CONTRAT;
        } else if (this.isDsnSignalRepriseSuiteArretTravail(dsn)) {
            return JSON_FILE_DSN_REPRISE_SUITE_ARRET_TRAVAIL;
        }

        return null;
    }

    private String getPathForPhase(Dsn dsn) {

        if (this.isDsnPhase2(dsn)) {
            return JSON_FOLDER_PHASE_02;
        } else if (this.isDsnPhase3(dsn)) {
            return JSON_FOLDER_PHASE_03;
        }
        return null;
    }

    private boolean isDsnMensuelle(Dsn dsn) {

        return dsn.getNature() != null && NATURE_MENSUELLE.equals(dsn.getNature());
    }

    private boolean isDsnSignalFinContrat(Dsn dsn) {
        return dsn.getNature() != null && NATURE_SIGNAL_FIN_CONTRAT.equals(dsn.getNature());
    }

    private boolean isDsnSignalArretTravail(Dsn dsn) {
        return dsn.getNature() != null && NATURE_SIGNAL_ARRET_TRAVAIL.equals(dsn.getNature());
    }

    private boolean isDsnSignalRepriseSuiteArretTravail(Dsn dsn) {
        return dsn.getNature() != null && NATURE_SIGNAL_REPRISE_SUITE_ARRET_TRAVAIL.equals(dsn.getNature());
    }

    public boolean isDsnPhase2(Dsn dsn) {
        return dsn.getPhase() != null && dsn.getPhase().startsWith(PREFIX_PHASE_2);
    }

    public boolean isDsnPhase3(Dsn dsn) {
        return dsn.getPhase() != null && dsn.getPhase().startsWith(PREFIX_PHASE_3);
    }

}
