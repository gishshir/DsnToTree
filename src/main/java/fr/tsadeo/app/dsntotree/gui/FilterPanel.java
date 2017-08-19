package fr.tsadeo.app.dsntotree.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import fr.tsadeo.app.dsntotree.model.BlocTree;
import fr.tsadeo.app.dsntotree.model.Dsn;
import fr.tsadeo.app.dsntotree.model.ItemBloc;

public class FilterPanel extends JPanel implements  IGuiConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final ActionListener actionListener;
	private final JPanel panelToggleButton = new JPanel();
	private final JPanel panelCheckboxes = new JPanel();
	
	private final ImageIcon imgIcoShow = GuiUtils.createImageIcon(PATH_SHOW_ICO);
	private final ImageIcon imgIcoHide = GuiUtils.createImageIcon(PATH_HIDE_ICO);
	
	private List<JCheckBox> listCheckboxes = new ArrayList<JCheckBox>();

	public FilterPanel(ActionListener actionListener) {
		this.actionListener = actionListener;
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder() );
		this.buildCheckboxPanel();
		this.buildTogglePanel();
		
		this.setPreferredSize(new Dimension(100, 600));
	}
	
	// --------------------------------------- public methods

	// --------------------------------------- private methods
	private void buildCheckboxPanel() {
		
		this.panelCheckboxes.setLayout(new BoxLayout(this.panelCheckboxes, BoxLayout.Y_AXIS));
		this.add(this.panelCheckboxes, BorderLayout.CENTER);
	}
	
	private void buildTogglePanel() {
		
		
		final JToggleButton  tgbExpandAll = new JToggleButton(imgIcoHide ,true);
		tgbExpandAll.setMargin(new Insets(2,2,2,2));
		tgbExpandAll.setSize(70, 50);
		tgbExpandAll.setActionCommand(ALL);
		tgbExpandAll.addActionListener(this.actionListener);
		tgbExpandAll.setEnabled(false);
		
		tgbExpandAll.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {

				tgbExpandAll.setIcon(tgbExpandAll.isSelected() ? imgIcoHide : imgIcoShow);

				selectAll(tgbExpandAll.isSelected());
			}
		});
		this.panelToggleButton.add(tgbExpandAll, BorderLayout.CENTER);
		this.add(this.panelToggleButton, BorderLayout.PAGE_START);
		
	}
	
    private void selectAll(boolean select) {
    	for (JCheckBox jCheckBox : listCheckboxes) {
			jCheckBox.setSelected(select);
		}
    }

	public void buildListBlocCheckbox(Dsn dsn) {

		this.panelToggleButton.getComponent(0).setEnabled(true);
		this.panelCheckboxes.removeAll();
		this.listCheckboxes.clear();

		Set<String> blocNames = new HashSet<String>();
		if (dsn.getBlocs() != null) {

			for (ItemBloc itemBloc : dsn.getBlocs()) {
				if (!blocNames.contains(itemBloc.getBlocLabel())) {
					blocNames.add(itemBloc.getBlocLabel());
				}
			}
			List<String> listBlocNames = new ArrayList<String>(blocNames);
			Collections.sort(listBlocNames);

			for (String blocLabel : listBlocNames) {

				BlocTree blocTree = dsn.getTreeRoot() == null?null:dsn.getTreeRoot().findChild(blocLabel, true);
				if (blocTree != null) {
					JCheckBox cb = new JCheckBox(blocTree.toString());
					cb.setSelected(true);
					cb.addActionListener(this.actionListener);
					this.listCheckboxes.add(cb);
					this.panelCheckboxes.add(cb);
					cb.setAlignmentX(CENTER_ALIGNMENT);
				}
			}

		}

	}
	

}
