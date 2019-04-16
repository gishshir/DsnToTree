package fr.tsadeo.app.dsntotree.model;

import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.util.IConstants;

public enum PhaseDsn implements IConstants {

    PHASE_2(PREFIX_PHASE_2, "Phase 2"), 
    PHASE_3(PREFIX_PHASE_3, "Phase 3"), 
    PHASE_2018(PREFIX_PHASE_18, "Phase 2018"),
    PHASE_19(PREFIX_PHASE_19, "Phase 19"),
    PHASE_20(PREFIX_PHASE_20, "Phase 20");

    private final String prefix;
    private final String libelle;

    private PhaseDsn(String prefix, String libelle) {
        this.prefix = prefix;
        this.libelle = libelle;
    }
    
    public static PhaseDsn getDefaut() {
    	return PHASE_20;
    }

    public static PhaseDsn getPhaseDsnFromPhase(String phase) {

        if (phase == null) {
            return null;
        }
        PhaseDsn natureDsn = null;
        for (PhaseDsn value : values()) {
            if (phase.startsWith(value.prefix)) {
                natureDsn = value;
                break;
            }
        }
        return natureDsn;
    }

    public static PhaseDsn getPhaseDsnFromPrefix(String prefix) {
        PhaseDsn natureDsn = null;
        for (PhaseDsn value : values()) {
            if (value.hasPrefix(prefix)) {
                natureDsn = value;
                break;
            }
        }
        return natureDsn;
    }

    public KeyAndLibelle getKeyAndLibelle() {
        return new KeyAndLibelle(this.prefix, this.libelle);
    }

    private boolean hasPrefix(String prefix) {
        return this.prefix.equals(prefix);
    }

    @Override
    public String toString() {
        return this.libelle;
    }

}
