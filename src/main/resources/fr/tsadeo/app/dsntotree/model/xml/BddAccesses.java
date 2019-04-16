//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2019.04.03 à 12:50:32 PM CEST 
//


package fr.tsadeo.app.dsntotree.model.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour BddAccesses complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="BddAccesses"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="oracleBdd" type="{}OracleBddAccess" maxOccurs="unbounded"/&gt;
 *         &lt;element name="postgreBdd" type="{}PostGreBddAccess" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BddAccesses", propOrder = {
    "oracleBdd",
    "postgreBdd"
})
public class BddAccesses {

    @XmlElement(required = true)
    protected List<OracleBddAccess> oracleBdd;
    protected List<PostGreBddAccess> postgreBdd;

    /**
     * Gets the value of the oracleBdd property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the oracleBdd property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOracleBdd().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OracleBddAccess }
     * 
     * 
     */
    public List<OracleBddAccess> getOracleBdd() {
        if (oracleBdd == null) {
            oracleBdd = new ArrayList<OracleBddAccess>();
        }
        return this.oracleBdd;
    }

    /**
     * Gets the value of the postgreBdd property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the postgreBdd property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPostgreBdd().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PostGreBddAccess }
     * 
     * 
     */
    public List<PostGreBddAccess> getPostgreBdd() {
        if (postgreBdd == null) {
            postgreBdd = new ArrayList<PostGreBddAccess>();
        }
        return this.postgreBdd;
    }

}
