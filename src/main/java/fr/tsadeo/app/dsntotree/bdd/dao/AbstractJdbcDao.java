package fr.tsadeo.app.dsntotree.bdd.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.tsadeo.app.dsntotree.bdd.model.EntiteBase;

public abstract class AbstractJdbcDao<T extends EntiteBase> {

    protected T getEntity(String sql) throws SQLException {

        System.out.println(sql);
        JdbcContainer jdbcContainer = null;
        try {

            jdbcContainer = this.prepareQuery(sql);
            if (jdbcContainer.getRs().next()) {
                return this.mapToEntity(Integer.MIN_VALUE, jdbcContainer.getRs());
            }
            return null;

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }

        finally {
            this.manageFinally(jdbcContainer);
        }
    }

    protected List<T> getListEntity(String sql) throws SQLException {

        System.out.println(sql);
        JdbcContainer jdbcContainer = null;
        try {

            jdbcContainer = this.prepareQuery(sql);
            List<T> list = new ArrayList<T>();
            int numline = 0;
            while (jdbcContainer.getRs().next()) {
                list.add(this.mapToEntity(numline++, jdbcContainer.getRs()));
            }
            return list;

        } finally {
            this.manageFinally(jdbcContainer);
        }
    }

    private JdbcContainer prepareQuery(String sql) throws SQLException {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        con = DatabaseManager.get().connect();
        stmt = con.createStatement();
        rs = stmt.executeQuery(sql);
        return new JdbcContainer(con, stmt, rs);

    }

    private void manageFinally(JdbcContainer jdbcContainer) {

        if (jdbcContainer == null) {
            return;
        }
        try {

            if (jdbcContainer.getRs() != null) {
                jdbcContainer.getRs().close();
            }
            if (jdbcContainer.getStmt() != null) {
                jdbcContainer.getStmt().close();
            }
            DatabaseManager.get().closeConnection(jdbcContainer.getCon());

        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    protected abstract T mapToEntity(int numline, ResultSet rs) throws SQLException;

    // ========================================= INNER CLASS
    private static class JdbcContainer {

        private final Connection con;
        private final Statement stmt;
        private final ResultSet rs;

        private Connection getCon() {
            return con;
        }

        private Statement getStmt() {
            return stmt;
        }

        private ResultSet getRs() {
            return rs;
        }

        public JdbcContainer(Connection con, Statement stmt, ResultSet rs) {
            this.con = con;
            this.stmt = stmt;
            this.rs = rs;
        }
    }

}
