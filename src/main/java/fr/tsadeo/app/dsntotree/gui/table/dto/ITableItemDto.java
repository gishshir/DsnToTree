package fr.tsadeo.app.dsntotree.gui.table.dto;

import fr.tsadeo.app.dsntotree.model.ItemBloc;

public interface ITableItemDto {

    public int getIndex();

    public boolean isVisible();

    public void setVisible(boolean visible);

    public String getValueForSearch();
    
    public ItemBloc getItemBloc();
    
    public String getNom();

}
