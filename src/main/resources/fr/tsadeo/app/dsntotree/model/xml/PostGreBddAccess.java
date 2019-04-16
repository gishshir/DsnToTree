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
 * <p>Classe Java pour PostGreBddAccess complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="PostGreBddAccess"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="instance" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="credentials" type="{}Credentials"/&gt;
 *         &lt;element name="defaut" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostGreBddAccess", propOrder = {
    "host",
    "port",
    "instance",
    "credentials",
    "defaut"
})
public class PostGreBddAccess {

    @XmlElement(required = true)
    protected String host;
    protected int port;
    @XmlElement(required = true)
    protected String instance;
    @XmlElement(required = true)
    protected Credentials credentials;
    protected Boolean defaut;

    /**
     * Obtient la valeur de la propriété host.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHost() {
        return host;
    }

    /**
     * Définit la valeur de la propriété host.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHost(String value) {
        this.host = value;
    }

    /**
     * Obtient la valeur de la propriété port.
     * 
     */
    public int getPort() {
        return port;
    }

    /**
     * Définit la valeur de la propriété port.
     * 
     */
    public void setPort(int value) {
        this.port = value;
    }

    /**
     * Obtient la valeur de la propriété instance.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstance() {
        return instance;
    }

    /**
     * Définit la valeur de la propriété instance.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstance(String value) {
        this.instance = value;
    }

    /**
     * Obtient la valeur de la propriété credentials.
     * 
     * @return
     *     possible object is
     *     {@link Credentials }
     *     
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Définit la valeur de la propriété credentials.
     * 
     * @param value
     *     allowed object is
     *     {@link Credentials }
     *     
     */
    public void setCredentials(Credentials value) {
        this.credentials = value;
    }

    /**
     * Obtient la valeur de la propriété defaut.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDefaut() {
        return defaut;
    }

    /**
     * Définit la valeur de la propriété defaut.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDefaut(Boolean value) {
        this.defaut = value;
    }

}
