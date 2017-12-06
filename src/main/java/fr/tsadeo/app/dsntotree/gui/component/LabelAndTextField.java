package fr.tsadeo.app.dsntotree.gui.component;

import java.awt.Dimension;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import fr.tsadeo.app.dsntotree.gui.IGuiConstants;
import fr.tsadeo.app.dsntotree.gui.action.PatternFilter;

public class LabelAndTextField extends JPanel implements IStateComponent, IGuiConstants {

	private static final long serialVersionUID = 1L;

	private final JLabel labKey;
	private final StateTextField tfValue;

	// -------------------------------- implementing IStateComponent
	@Override
	public void waitEndAction() {
		this.tfValue.waitEndAction();
	}

	@Override
	public void actionEnded() {
		this.tfValue.actionEnded();
	}

	// -------------------------------------- constructor
	public LabelAndTextField(String text, int labelLenght, int boxLenght) {
		this(text, labelLenght, boxLenght, true);
	}

	public LabelAndTextField(String text, int labelLenght, int boxLenght, boolean editable) {
		this(text, labelLenght, boxLenght, editable, null);
	}

	public LabelAndTextField(String text, int labelLenght, int boxLenght, boolean editable, Pattern pattern) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setPreferredSize(new Dimension(labelLenght + boxLenght + 10, 25));
		
		this.labKey = new JLabel(text);
		Dimension dimKey = new Dimension(labelLenght, 20);
		this.labKey.setPreferredSize(dimKey);
		this.labKey.setMaximumSize(dimKey);

		this.tfValue = new StateTextField();
		Dimension dimValue = new Dimension(boxLenght, 20);
		this.tfValue.setPreferredSize(dimValue);
		this.tfValue.setMaximumSize(dimValue);
		this.tfValue.setEditable(editable);
		

		this.add(this.labKey);
		this.add(Box.createRigidArea(DIM_HOR_RIGID_AREA_10));
		this.add(this.tfValue);

		if (pattern != null) {
			((AbstractDocument) this.tfValue.getDocument()).setDocumentFilter(new PatternFilter(pattern));
		}

		this.add(Box.createHorizontalGlue());
	}

	// -------------------------------------- public methods
	public void setDocumentListener(DocumentListener documentListener) {
		if (this.tfValue != null) {
			this.tfValue.getDocument().addDocumentListener(documentListener);
		}
	}

	public void setEnabled(boolean enabled) {
		this.tfValue.setEnabled(enabled);
	}

	public boolean isEmpty() {
		return this.getValue() == null || this.getValue().trim().isEmpty();
	}

	public void setValue(String value) {
		this.tfValue.setText(value);
	}

	public String getValue() {
		return this.tfValue.getText();
	}

	public Integer getIntValue() {

		Integer intValue = null;
		if (!this.isEmpty()) {
			try {
				intValue = Integer.parseInt(this.tfValue.getText().trim());
			} catch (NumberFormatException ex) {
				// Nothing
			}
		}
		return intValue;
	}

}
