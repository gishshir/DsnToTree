package fr.tsadeo.app.dsntotree.bdd.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import fr.tsadeo.app.dsntotree.bdd.dao.AbstractJdbcDao;
import fr.tsadeo.app.dsntotree.bdd.dao.IDataDsnDao;
import fr.tsadeo.app.dsntotree.bdd.model.DataDsn;
import fr.tsadeo.app.dsntotree.util.StringUtils;

public class JdbcDataDsnDao extends AbstractJdbcDao<DataDsn> implements IDataDsnDao {

    private static final String DSDOCHRMSG = "DSDOCHRMSG"; // chronomessage
    private static final String DSDOIDFSYS = "DSDOIDFSYS"; // idfsys
    private static final String DSDOSEQBLC = "DSDOSEQBLC"; // seq_bloc
    private static final String DSDOSEQSUP = "DSDOSEQSUP"; // seq_sup
    private static final String DSDOCODRUB = "DSDOCODRUB"; // codeRubrique
    private static final String DSDOVALRUB = "DSDOVALRUB"; // value
    private static final String DSDOBLCRAT = "DSDOBLCRAT"; // bloc

    private static final String DSDONNEESCL = "DSDONNEESCL "; // table DSDO

    private static final String SQL_LIST_DATA_FOR_MSG = StringUtils.concat(SELECT, DSDOIDFSYS, v, DSDOBLCRAT, v,
            DSDOSEQBLC, v, DSDOSEQSUP, v, DSDOCODRUB, v, DSDOVALRUB, FROM, DSDONNEESCL, WHERE, DSDOCHRMSG, "= %1$d ",
            ORDER_BY, DSDOBLCRAT, v, DSDOSEQBLC, v, DSDOSEQSUP, v, DSDOCODRUB);

    // ------------------------------------ implementing DataDsnDao
    @Override
    public List<DataDsn> getListDataDsnForMessage(Long chronoMessage) throws SQLException {
        String sql = String.format(SQL_LIST_DATA_FOR_MSG, chronoMessage);
        return super.getListEntity(sql);
    }

    // ------------------------------------ implementing AbstractJdbcDao
    @Override
    protected DataDsn mapToEntity(int numline, ResultSet rs) throws SQLException {

        DataDsn data = new DataDsn();
        data.setNumLine(numline);
        data.setId(rs.getLong(DSDOIDFSYS));
        data.setBloc(rs.getString(DSDOBLCRAT));
        data.setNumSequenceBloc(rs.getInt(DSDOSEQBLC));
        data.setNumSequenceBlocSup(rs.getInt(DSDOSEQSUP));
        data.setCodeRubrique(rs.getString(DSDOCODRUB));
        data.setValue(rs.getString(DSDOVALRUB));

        return data;
    }

}
