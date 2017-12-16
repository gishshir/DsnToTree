package fr.tsadeo.app.dsntotree.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import fr.tsadeo.app.dsntotree.gui.IBlocActionListener;
import fr.tsadeo.app.dsntotree.gui.MyPanelBloc.PanelRubrique;
import fr.tsadeo.app.dsntotree.gui.component.IFunctionnalChild;

abstract class AbstractPanelRubriqueAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	protected final IBlocActionListener blocActionListener;

	protected AbstractPanelRubriqueAction(IBlocActionListener blocActionListener) {
		this.blocActionListener = blocActionListener;
	}


    protected PanelRubrique getPanelRubrique(ActionEvent ev) {
        if (ev.getSource() != null) {

            if (ev.getSource() instanceof IFunctionnalChild) {
                IFunctionnalChild source = (IFunctionnalChild) ev.getSource();
                if (source.getFunctionnalContainer() != null
                        && source.getFunctionnalContainer() instanceof PanelRubrique) {
                    return (PanelRubrique) source.getFunctionnalContainer();
                }
            }

        }
        return null;
    }
}