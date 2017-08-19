package fr.tsadeo.app.dsntotree.bdd.dao;

import java.sql.SQLException;
import java.util.List;

import fr.tsadeo.app.dsntotree.bdd.model.MessageDsn;

public interface IMessageDsnDao extends ISqlDao {

    public MessageDsn getMessageDsn(Long chronoMessage) throws SQLException;

    public List<MessageDsn> getListMessageDsn() throws SQLException;
}
