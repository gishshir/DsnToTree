//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2019.04.03 à 12:50:32 PM CEST 
//


package fr.tsadeo.app.dsntotree.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour Bdd complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Bdd"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bddAccesses" type="{}BddAccesses"/&gt;
 *         &lt;element name="sqls" type="{}Sqls"/&gt;
 *         &lt;element name="services" type="{}BddServices"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Bdd", propOrder = {
    "bddAccesses",
    "sqls",
    "services"
})
public class Bdd {

    @XmlElement(required = true)
    protected BddAccesses bddAccesses;
    @XmlElement(required = true)
    protected Sqls sqls;
    @XmlElement(required = true)
    protected BddServices services;

    /**
     * Obtient la valeur de la propriété bddAccesses.
     * 
     * @return
     *     possible object is
     *     {@link BddAccesses }
     *     
     */
    public BddAccesses getBddAccesses() {
        return bddAccesses;
    }

    /**
     * Définit la valeur de la propriété bddAccesses.
     * 
     * @param value
     *     allowed object is
     *     {@link BddAccesses }
     *     
     */
    public void setBddAccesses(BddAccesses value) {
        this.bddAccesses = value;
    }

    /**
     * Obtient la valeur de la propriété sqls.
     * 
     * @return
     *     possible object is
     *     {@link Sqls }
     *     
     */
    public Sqls getSqls() {
        return sqls;
    }

    /**
     * Définit la valeur de la propriété sqls.
     * 
     * @param value
     *     allowed object is
     *     {@link Sqls }
     *     
     */
    public void setSqls(Sqls value) {
        this.sqls = value;
    }

    /**
     * Obtient la valeur de la propriété services.
     * 
     * @return
     *     possible object is
     *     {@link BddServices }
     *     
     */
    public BddServices getServices() {
        return services;
    }

    /**
     * Définit la valeur de la propriété services.
     * 
     * @param value
     *     allowed object is
     *     {@link BddServices }
     *     
     */
    public void setServices(BddServices value) {
        this.services = value;
    }

}
