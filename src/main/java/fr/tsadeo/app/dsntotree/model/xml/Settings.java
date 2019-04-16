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
 * <p>Classe Java pour Settings complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Settings"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dsn" type="{}Dsn"/&gt;
 *         &lt;element name="bdd" type="{}Bdd" minOccurs="0"/&gt;
 *         &lt;element name="norme" type="{}Norme" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Settings", propOrder = {
    "dsn",
    "bdd",
    "norme"
})
public class Settings {

    @XmlElement(required = true)
    protected Dsn dsn;
    protected Bdd bdd;
    protected Norme norme;

    /**
     * Obtient la valeur de la propriété dsn.
     * 
     * @return
     *     possible object is
     *     {@link Dsn }
     *     
     */
    public Dsn getDsn() {
        return dsn;
    }

    /**
     * Définit la valeur de la propriété dsn.
     * 
     * @param value
     *     allowed object is
     *     {@link Dsn }
     *     
     */
    public void setDsn(Dsn value) {
        this.dsn = value;
    }

    /**
     * Obtient la valeur de la propriété bdd.
     * 
     * @return
     *     possible object is
     *     {@link Bdd }
     *     
     */
    public Bdd getBdd() {
        return bdd;
    }

    /**
     * Définit la valeur de la propriété bdd.
     * 
     * @param value
     *     allowed object is
     *     {@link Bdd }
     *     
     */
    public void setBdd(Bdd value) {
        this.bdd = value;
    }

    /**
     * Obtient la valeur de la propriété norme.
     * 
     * @return
     *     possible object is
     *     {@link Norme }
     *     
     */
    public Norme getNorme() {
        return norme;
    }

    /**
     * Définit la valeur de la propriété norme.
     * 
     * @param value
     *     allowed object is
     *     {@link Norme }
     *     
     */
    public void setNorme(Norme value) {
        this.norme = value;
    }

}
