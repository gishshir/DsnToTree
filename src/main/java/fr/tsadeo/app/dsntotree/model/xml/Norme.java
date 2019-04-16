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
 * <p>Classe Java pour Norme complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Norme"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dsnnormefile" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
@XmlType(name = "Norme", propOrder = {
    "dsnnormefile",
    "actif"
})
public class Norme {

    @XmlElement(required = true)
    protected String dsnnormefile;
    protected boolean actif;

    /**
     * Obtient la valeur de la propriété dsnnormefile.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDsnnormefile() {
        return dsnnormefile;
    }

    /**
     * Définit la valeur de la propriété dsnnormefile.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDsnnormefile(String value) {
        this.dsnnormefile = value;
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
