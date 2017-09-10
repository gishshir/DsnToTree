package fr.tsadeo.app.dsntotree.bdd.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import fr.tsadeo.app.dsntotree.bdd.dao.AbstractJdbcDao;
import fr.tsadeo.app.dsntotree.bdd.dao.IMessageDsnDao;
import fr.tsadeo.app.dsntotree.bdd.model.MessageDsn;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class JdbcMessageDsnDao extends AbstractJdbcDao<MessageDsn> implements IMessageDsnDao {
	
	private static final Logger LOG = Logger.getLogger(JdbcMessageDsnDao.class.getName());

    private static final String DSMSCHRMSG = "DSMSCHRMSG"; // chrono message
    private static final String DSMSDATDCL = "DSMSDATDCL"; // date ref
    private static final String DSMSNOMMSG = "DSMSNOMMSG"; // nom fichier
                                                           // message
    private static final String DSMSGDSNCL = "DSMSGDSNCL"; // table

    private String SQL_GET_MESS_BY_CHRONO = StringUtils.concat(SELECT, DSMSCHRMSG, v, DSMSDATDCL, v, DSMSNOMMSG, FROM,
            DSMSGDSNCL, WHERE, DSMSCHRMSG, " = %1$d");

    @Override
    public List<MessageDsn> getListMessageDsn() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MessageDsn getMessageDsn(Long chronoMessage) throws SQLException {
        String sql = String.format(SQL_GET_MESS_BY_CHRONO, chronoMessage);
        return super.getEntity(sql);
    }

    // ------------------------------------ implementing AbstractJdbcDao
	@Override
	protected Logger getLogger() {
		return LOG;
	}

    
    @Override
    protected MessageDsn mapToEntity(int numline, ResultSet rs) throws SQLException {

        MessageDsn messageDsn = new MessageDsn();

        messageDsn.setId(rs.getLong("DSMSCHRMSG"));
        messageDsn.setNumeroChronoMessage(rs.getLong("DSMSCHRMSG"));
        messageDsn.setDateReferenceDeclaration(rs.getDate("DSMSDATDCL"));
        messageDsn.setName(rs.getString("DSMSNOMMSG"));

        return messageDsn;
    }

}
