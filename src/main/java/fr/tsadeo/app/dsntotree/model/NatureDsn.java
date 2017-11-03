package fr.tsadeo.app.dsntotree.model;

import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public enum NatureDsn {

    /** The dsn mensuelle. */
    DSN_MENSUELLE("01", "Mensuelle"),

    /** The dsn signal fin contrat. */
    DSN_SIGNAL_FIN_CONTRAT("02", "Signalement de fin de contrat"),

    /** The dsn signal arret travail. */
    DSN_SIGNAL_ARRET_TRAVAIL("04", "Signalement d'arrêt de travail"),

    /** The dsn reprise suite arret travail. */
    DSN_REPRISE_SUITE_ARRET_TRAVAIL("05", "Signalement de reprise de travail");

    /** The libelle. */
    private final String code;

    private final String libelle;

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }

    private NatureDsn(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public static NatureDsn getNatureDsn(String code) {
        NatureDsn natureDsn = null;
        for (NatureDsn value : values()) {
            if (value.hasCode(code)) {
                natureDsn = value;
                break;
            }
        }
        return natureDsn;
    }

    public KeyAndLibelle getKeyAndLibelle() {
        return new KeyAndLibelle(this.code, this.libelle);
    }
    private boolean hasCode(String code) {
        return this.code.equals(code);
    }

    @Override
    public String toString() {
        return StringUtils.concat(this.libelle, " (", this.code, ")");
    }
}
