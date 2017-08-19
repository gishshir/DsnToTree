package fr.tsadeo.app.dsntotree.bdd.dao;

import java.sql.SQLException;
import java.util.List;

import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;

public interface IDataDsnDao extends ISqlDao {

    public List<DataDsn> getListDataDsnForMessage(Long chronoMessage) throws SQLException;

}
