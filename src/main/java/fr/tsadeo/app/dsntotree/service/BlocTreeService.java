package fr.tsadeo.app.dsntotree.service;

import java.util.logging.Logger;

public class BlocTreeService extends AbstractReadDsn {

    private static final Logger LOG = Logger.getLogger(BlocTreeService.class.getName());

    @Override
    protected Logger getLog() {
        return LOG;
    }

}
