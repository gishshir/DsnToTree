package fr.tsadeo.app.dsntotree.gui.salarie;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import fr.tsadeo.app.dsntotree.business.SalarieDto;

public class TableSalaries extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Column[] tabColumns = new Column[]{
			new Column(0, 5, "index"),
			new Column(1, 20, "Nir"),
			new Column(2, 50, "Nom"),
			new Column(3, 75, "Prénoms")			
	};

	private final SalariesTableModel model;
	private final ISalarieListener salarieListener;

	TableSalaries(ISalarieListener salarieListener) {

		super(new SalariesTableModel(tabColumns), new MyColumnModel(tabColumns));
		this.model = (SalariesTableModel) super.getModel();
		this.salarieListener = salarieListener;
		
		this.buildListSelectionListener();

		this.setPreferredScrollableViewportSize(new Dimension(300, 70));
		this.setFillsViewportHeight(true);

	}

	void setDatas(List<SalarieDto> listSalaries) {

		this.model.setDatas(listSalaries);
	}
	
	//---------------------------------- private methods
	private void buildListSelectionListener() {

		this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				int[] selectedRows = TableSalaries.this.getSelectedRows();
				if (selectedRows != null && selectedRows.length == 1) {
					SalarieDto salarie = model.getSalarie(selectedRows[0]);
					salarieListener.onSalarieSelected(salarie);
				}
			}
		});
	}

	
		
	// =================================== INNER CLASS
	private static class MyColumnModel extends DefaultTableColumnModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private MyColumnModel(Column[] tabColumns) {

			for (Column column : tabColumns) {
				TableColumn tableColumn = new TableColumn(column.index, column.width);
				tableColumn.setHeaderValue(column.title);
				this.addColumn(tableColumn);
			};
		}
	}

	 static class Column {

		private final int index;
		private final int width;
		private final String title;

		 int getIndex() {
			return index;
		}

		 int getWidth() {
			return width;
		}

		 String getTitle() {
			return title;
		}

		private Column(int index, int width, String title) {
			this.index = index;
			this.width = width;
			this.title = title;
		}
	}
}
