package fr.tsadeo.app.dsntotree.gui.table.common;

import java.util.List;

import javax.swing.table.TableModel;

import fr.tsadeo.app.dsntotree.gui.table.dto.ITableItemDto;

public interface IMyTableModel<T extends ITableItemDto> extends TableModel {

    public List<T> getDatas();

    public void setDatas(List<T> listItems);

    public boolean filter(String search);

    public void reinitSearch();

    public T getItem(int rowIndex);

}
