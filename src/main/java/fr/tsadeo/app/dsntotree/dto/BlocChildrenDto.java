package fr.tsadeo.app.dsntotree.dto;

import java.util.ArrayList;
import java.util.List;

public class BlocChildrenDto {

	private final List<String> listOtherBlocLabel = new ArrayList<String>();
	private final List<BlocChildDto> listBlocChildDto = new ArrayList<BlocChildDto>();
	public List<String> getListOtherBlocLabel() {
		return listOtherBlocLabel;
	}
	public List<BlocChildDto> getListBlocChildDto() {
		return listBlocChildDto;
	}
	
	public boolean hasChild() {
		return !this.listBlocChildDto.isEmpty();
	}
	
	public boolean hasOtherChild()  {
		return !this.listOtherBlocLabel.isEmpty();
	}
	
	public boolean canHaveChildren() {
		return this.hasChild() || this.hasOtherChild();
	}
}
