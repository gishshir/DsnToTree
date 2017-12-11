package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IBlocActionListener;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelChild;
import fr.tsadeo.app.dsntotree.gui.component.IFunctionnalChild;

abstract class AbstractPanelBlocAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    protected final IBlocActionListener blocActionListener;

    protected AbstractPanelBlocAction(IBlocActionListener blocActionListener) {
        this.blocActionListener = blocActionListener;
    }

    protected PanelChild getPanelChild(ActionEvent ev) {

        if (ev.getSource() != null && ev.getSource() instanceof IFunctionnalChild) {
            IFunctionnalChild source = (IFunctionnalChild) ev.getSource();
            return (PanelChild) source.getFunctionnalContainer();

        }
        return null;
    }
}