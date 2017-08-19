package fr.tsadeo.app.dsntotree.model;

public enum CardinaliteEnum {
	
	UN (1,1),
	ZERO_OU_UN (0, 1),
	AU_MOINS_UN (1, Integer.MAX_VALUE),
	TOUS (0, Integer.MAX_VALUE);
	
	private final int min;
	private final int max;
	
	
	
	public int getMin() {
		return min;
	}



	public int getMax() {
		return max;
	}



	private CardinaliteEnum(int min, int max) {
		this.min = min;
		this.max = max;
	}

}
