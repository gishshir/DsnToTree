package fr.tsadeo.app.dsntotree.model;

import java.util.Objects;

public class PhaseNatureType {

    private PhaseDsn phase;
    private NatureDsn nature;
    private TypeDsn type;

    // ----------------------------- accessors
    public PhaseDsn getPhase() {
        return phase;
    }

    public void setPhase(PhaseDsn phase) {
        this.phase = phase;
    }

    public NatureDsn getNature() {
        return nature;
    }

    public void setNature(NatureDsn nature) {
        this.nature = nature;
    }

    public TypeDsn getType() {
        return type;
    }

    public void setType(TypeDsn type) {
        this.type = type;
    }

    public PhaseNatureType() {
    }

    public PhaseNatureType(PhaseDsn phaseDsn, NatureDsn natureDsn, TypeDsn typeDsn) {

        this.phase = phaseDsn;
        this.nature = natureDsn;
        this.type = typeDsn;
    }

    // --------------------------------- overriding Object

    @Override
    public int hashCode() {
        final int prime = 13;
        int result = 1;
        result = prime * result + ((phase == null) ? 0 : phase.hashCode());
        result = prime * result + ((nature == null) ? 0 : nature.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PhaseNatureType)) {
            return false;
        }
        PhaseNatureType pnt = (PhaseNatureType) obj;

        return Objects.equals(phase, pnt.phase) && Objects.equals(nature, nature) && Objects.equals(type, type);

    }

}
