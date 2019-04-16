//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2019.04.03 à 12:50:32 PM CEST 
//


package fr.tsadeo.app.dsntotree.model.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fr.tsadeo.app.dsntotree.model.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Settings_QNAME = new QName("", "settings");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.tsadeo.app.dsntotree.model.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Settings }
     * 
     */
    public Settings createSettings() {
        return new Settings();
    }

    /**
     * Create an instance of {@link Dsn }
     * 
     */
    public Dsn createDsn() {
        return new Dsn();
    }

    /**
     * Create an instance of {@link Norme }
     * 
     */
    public Norme createNorme() {
        return new Norme();
    }

    /**
     * Create an instance of {@link Bdd }
     * 
     */
    public Bdd createBdd() {
        return new Bdd();
    }

    /**
     * Create an instance of {@link BddServices }
     * 
     */
    public BddServices createBddServices() {
        return new BddServices();
    }

    /**
     * Create an instance of {@link BddAccesses }
     * 
     */
    public BddAccesses createBddAccesses() {
        return new BddAccesses();
    }

    /**
     * Create an instance of {@link OracleBddAccess }
     * 
     */
    public OracleBddAccess createOracleBddAccess() {
        return new OracleBddAccess();
    }

    /**
     * Create an instance of {@link PostGreBddAccess }
     * 
     */
    public PostGreBddAccess createPostGreBddAccess() {
        return new PostGreBddAccess();
    }

    /**
     * Create an instance of {@link Credentials }
     * 
     */
    public Credentials createCredentials() {
        return new Credentials();
    }

    /**
     * Create an instance of {@link Credential }
     * 
     */
    public Credential createCredential() {
        return new Credential();
    }

    /**
     * Create an instance of {@link Sqls }
     * 
     */
    public Sqls createSqls() {
        return new Sqls();
    }

    /**
     * Create an instance of {@link Sql }
     * 
     */
    public Sql createSql() {
        return new Sql();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Settings }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "settings")
    public JAXBElement<Settings> createSettings(Settings value) {
        return new JAXBElement<Settings>(_Settings_QNAME, Settings.class, null, value);
    }

}
