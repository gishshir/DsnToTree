package fr.tsadeo.app.dsntotree.bdd.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import fr.tsadeo.app.dsntotree.bdd.dao.IBddAccessManager.Type;
import fr.tsadeo.app.dsntotree.dto.BddConnexionDto;

public class DatabaseManager {

//    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
//    private static final String DB_URL = "jdbc:oracle:thin:@dedtanya04.ext.tdc:1521/INFIDEV";
//    private static final String DB_USER = "IDEV00_USER";
//    private static final String DB_PWD = "IDEV00_USER";

    private static DatabaseManager instance;

    public static DatabaseManager get() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private Set<String> bagOfLoadedDriver = new HashSet<String>();

    private DatabaseManager() {

    }

    private boolean loadDriver(String driver) {

        if (bagOfLoadedDriver.contains(driver)) {
            return true;
        }
        boolean loadOk = false;
        try {
            Class.forName(driver);
            System.out.println("Driver OK...");
            this.bagOfLoadedDriver.add(driver);
            loadOk = true;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver Nok...");
            e.printStackTrace();
        }

        return loadOk;
    }

    public BddConnexionDto getDefaultBddConnexionDto() {

        return BddAccessManagerFactory.get(Type.Oracle).getDefaultBddConnexionDto();

    }

    public boolean testerConnexion(BddConnexionDto connexionDto) {

        if (connexionDto == null) {
            return false;
        }
        boolean result = false;
        if (this.loadDriver(connexionDto.getDriver())) {

            Connection con = null;
            try {

                con = this.connect(connexionDto);
                result = true;
            } catch (SQLException ex) {
                System.err.println("Echec de connexion...");
            }

            finally {
                this.closeConnection(con);
            }
        }

        return result;
    }

    public Connection connect(BddConnexionDto connexionDto) throws SQLException {
        if (connexionDto == null) {
            return null;
        }
        Connection con = DriverManager.getConnection(connexionDto.getUrl(), connexionDto.getUser(),
                connexionDto.getPwd());
        System.out.println("connection openned...");
        return con;
    }

    public Connection connect() throws SQLException {

        return this.connect(BddAccessManagerFactory.get().getCurrentBddConnexionDto());
    }

    public void closeConnection(Connection con) {
        try {
            if (con != null && !con.isClosed()) {

                con.close();
                System.out.println("...connection closed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
