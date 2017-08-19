package fr.tsadeo.app.dsntotree.bdd.model;

import java.util.Date;

import fr.tsadeo.app.dsntotree.util.StringUtils;

public class MessageDsn extends EntiteBase {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long numeroChronoMessage;
    private Date dateReferenceDeclaration;
    private String name;

    // ------------------------------------------ accessors
    public Long getNumeroChronoMessage() {
        return numeroChronoMessage;
    }

    public void setNumeroChronoMessage(Long numeroChronoMessage) {
        this.numeroChronoMessage = numeroChronoMessage;
    }

    public Date getDateReferenceDeclaration() {
        return dateReferenceDeclaration;
    }

    public void setDateReferenceDeclaration(Date dateReferenceDeclaration) {
        this.dateReferenceDeclaration = dateReferenceDeclaration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return StringUtils.concat("Chrono: ", this.getNumeroChronoMessage(), " - date ", this.dateReferenceDeclaration,
                " - name: ", this.name);
    }

}
