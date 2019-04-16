package fr.tsadeo.app.dsntotree.util;

import java.io.InputStream;

import fr.tsadeo.app.dsntotree.model.NatureDsn;
import fr.tsadeo.app.dsntotree.model.PhaseDsn;
import fr.tsadeo.app.dsntotree.model.PhaseNatureType;

public class JsonUtils implements IJsonConstants, IConstants {

    public InputStream getJsonForDsnAsStream(PhaseNatureType phaseNatureType) {

        String path = this.getPathForPhase(phaseNatureType.getPhase());
        String filename = this.getFilenameForNature(phaseNatureType.getNature());
        return path == null || filename == null ? null : this.getJsonFileAsStream(path, filename);
    }

    public InputStream getJsonEnteteForDsnAsStream(PhaseDsn phase) {

        String path = this.getPathForPhase(phase);
        return path == null ? null : this.getJsonFileAsStream(path, JSON_FILE_DSN_ENTETE);
    }

    private InputStream getJsonFileAsStream(String path, String filename) {

        if (filename == null || path == null) {
            throw new RuntimeException("Impossible de localiser le fichier json !");
        }
        String completePath = path.concat(filename);

        return this.getClass().getClassLoader().getResourceAsStream(completePath);

    }

    private String getFilenameForNature(NatureDsn nature) {

        if (nature == null) {
            return null;
        }

        switch (nature) {
        case DSN_MENSUELLE:
            return JSON_FILE_DSN_MENSUELLE;
        case DSN_SIGNAL_ARRET_TRAVAIL:
            return JSON_FILE_DSN_SIGNAL_ARRET_TRAVAIL;
        case DSN_REPRISE_SUITE_ARRET_TRAVAIL:
            return JSON_FILE_DSN_REPRISE_SUITE_ARRET_TRAVAIL;
        case DSN_SIGNAL_FIN_CONTRAT:
            return JSON_FILE_DSN_SIGNAL_FIN_CONTRAT;
        }
        return null;
    }

    private String getPathForPhase(PhaseDsn phaseDsn) {

        switch (phaseDsn) {
        case PHASE_2:
            return JSON_FOLDER_PHASE_02;
        case PHASE_3:
        case PHASE_2018:
            return JSON_FOLDER_PHASE_03;
        case PHASE_19:
            return JSON_FOLDER_PHASE_19;
        case PHASE_20:
            return JSON_FOLDER_PHASE_20;
        }
        return null;
    }

}
