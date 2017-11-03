package fr.tsadeo.app.dsntotree.model;

import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public enum TypeDsn {

    NORMALE("01", "Normale"), // ...
    NORMALE_NEANT("02", "Normale néant"), // ...
    ANNULE_ET_REMPLACE_INTEGRAL("03", "Annule et remplace intégral"), // ...
    ANNULE("04", "Annule"), // ...
    ANNULE_ET_REMPLACER_NEANT("05", "Annule et remplace néant"); // ...

    private final String code;
    private final String libelle;

    private TypeDsn(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }

    public static TypeDsn getNatureDsn(String code) {
        TypeDsn typeDsn = null;
        for (TypeDsn value : values()) {
            if (value.hasCode(code)) {
                typeDsn = value;
                break;
            }
        }
        return typeDsn;
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
