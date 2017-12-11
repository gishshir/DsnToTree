package fr.tsadeo.app.dsntotree.gui.component;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;

import fr.tsadeo.app.dsntotree.dico.KeyAndLibelle;

public class StateListBox extends JList<KeyAndLibelle> implements IStateComponent, IFunctionnalChild {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private StateComponentEnum state = StateComponentEnum.actif;
    private long resultCount = 0L;
    private JComponent functionnalContainer;
    
    
	@Override
	public void setEnabled(boolean enabled) {

		this.state = enabled ? StateComponentEnum.actif : StateComponentEnum.inactif;
		super.setEnabled(enabled);
	}
	
	//------------------------------- constructor
	public StateListBox() {

		super(new DefaultListModel<KeyAndLibelle>());
	}

	// -------------------------------- implementing IStateComponent
	@Override
	public void waitEndAction() {
		super.setEnabled(false);
	}

	@Override
	public void actionEnded() {
		super.setEnabled(this.state == StateComponentEnum.actif);
	}

    public void setFunctionnalContainer(JComponent component) {
        this.functionnalContainer = component;
    }

    @Override
    public JComponent getFunctionnalContainer() {
        return functionnalContainer;
    }

	//----------------------------------------- public methods
	public void removeAllElements() {
		this.getDefaultListModel().removeAllElements();
	}
	public void populateListBox(List<KeyAndLibelle> listItems) {
		resultCount = listItems == null ? 0 : listItems.size();

		if (listItems != null) {

            System.out.println("nbr instances: " + resultCount);

            DefaultListModel<KeyAndLibelle> model = getDefaultListModel();
            model.removeAllElements();

            for (KeyAndLibelle keyAndLibelle : listItems) {
                model.addElement(keyAndLibelle);
            }

            System.out.println("model rempli!");
        }
	
	}

	//---------------------------------- private methods
	private DefaultListModel<KeyAndLibelle> getDefaultListModel() {
		return (DefaultListModel<KeyAndLibelle>)super.getModel();
	}
}
