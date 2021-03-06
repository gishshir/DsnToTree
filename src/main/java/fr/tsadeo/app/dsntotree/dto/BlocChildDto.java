package fr.tsadeo.app.dsntotree.dto;

import fr.tsadeo.app.dsntotree.model.ItemBloc;

public class BlocChildDto {
	
	private final ItemBloc blocChild;
	
	private boolean show = true;
	private boolean del = true;
	private boolean duplicate = true;
	
	
	public ItemBloc getBlocChild() {
		return blocChild;
	}


	public boolean isShow() {
		return show;
	}


	public void setShow(boolean show) {
		this.show = show;
	}


	public boolean isDel() {
		return del;
	}


	public void setDel(boolean del) {
		this.del = del;
	}


	public BlocChildDto(ItemBloc blocChild) {
		this.blocChild = blocChild;
	}


	public boolean isDuplicate() {
		return duplicate;
	}


	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}
	

}
