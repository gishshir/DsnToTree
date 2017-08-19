package fr.tsadeo.app.dsntotree.bdd.model;

import java.io.Serializable;

public abstract class EntiteBase implements Serializable {

    /**
     * Tue UID.
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
