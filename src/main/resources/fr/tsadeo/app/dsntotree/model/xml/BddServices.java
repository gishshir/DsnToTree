//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2019.04.03 à 12:50:32 PM CEST 
//


package fr.tsadeo.app.dsntotree.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour BddServices complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="BddServices"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="oracleServices" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="postgreServices" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="actif" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BddServices", propOrder = {
    "oracleServices",
    "postgreServices",
    "actif"
})
public class BddServices {

    protected String oracleServices;
    protected String postgreServices;
    protected boolean actif;

    /**
     * Obtient la valeur de la propriété oracleServices.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOracleServices() {
        return oracleServices;
    }

    /**
     * Définit la valeur de la propriété oracleServices.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOracleServices(String value) {
        this.oracleServices = value;
    }

    /**
     * Obtient la valeur de la propriété postgreServices.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostgreServices() {
        return postgreServices;
    }

    /**
     * Définit la valeur de la propriété postgreServices.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostgreServices(String value) {
        this.postgreServices = value;
    }

    /**
     * Obtient la valeur de la propriété actif.
     * 
     */
    public boolean isActif() {
        return actif;
    }

    /**
     * Définit la valeur de la propriété actif.
     * 
     */
    public void setActif(boolean value) {
        this.actif = value;
    }

}
