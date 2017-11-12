package fr.tsadeo.app.dsntotree.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils  implements IRegexConstants{
	
	  private static RegexUtils instance;

	    public static RegexUtils get() {
	        if (instance == null) {
	            instance = new RegexUtils();
	        }
	        return instance;
	    }


	    private RegexUtils() {
	    }


	
	public  boolean matches(String value, Pattern pattern) {
		
		Matcher matcher = getMatcher(value, pattern);
		return matcher == null?false:pattern.matcher(value).matches();
	}
	
	public  boolean matches(String value, Pattern pattern, int groupCount) {
		
		Matcher matcher = getMatcher(value, pattern);
		if (matcher != null && matcher.matches()) {
			return matcher.groupCount() == groupCount;
		}
		return false;
	}
	
	public  void extractsGroups(String value, Pattern pattern, CapturingGroups capturingGroups) {
		Matcher matcher = getMatcher(value, pattern);
		
		if (matcher != null && matcher.matches()) {
			Set<Integer> groups = capturingGroups.getGroups();
            for (Integer group : groups) {
            	if (matcher.groupCount() >= group) {
					capturingGroups.setValue(group,  matcher.group(group));
				}
			}
			
		}
	}
	public  String[] extractsGroups(String value, Pattern pattern, int... groupIndex) {
		Matcher matcher = getMatcher(value, pattern);
		String[] groups = null;
		
		if (matcher != null && matcher.matches()) {
			groups = new String[groupIndex == null?0:groupIndex.length];
            for (int i = 0; i < groups.length; i++) {
				int index = groupIndex[i];
				if (matcher.groupCount() >= index) {
					groups[i] = matcher.group(index);
				}	
			}
			
		}
		return groups;
	}
	
	private  Matcher getMatcher(String value, Pattern pattern) {

		if (value == null || pattern == null) {
			return null;
		}
		return pattern.matcher(value);
	}
	
	//=========================================== INNER CLASS
	public static class CapturingGroups {
		
		private final Map<Integer, String> mapGroupIndexToValue = new HashMap<>();
		
		public CapturingGroups(Integer... groupIndexes) {
			if (groupIndexes != null) {
				for (Integer groupIndex : groupIndexes) {
					this.mapGroupIndexToValue.put(groupIndex, null);
				}
			}
		}
		public String valueOf(Integer groupIndex) {
			return this.mapGroupIndexToValue.get(groupIndex);
		}
		public Set<Integer> getGroups() {
			return this.mapGroupIndexToValue.keySet();
		}
		private void setValue(Integer groupIndex, String value) {
			this.mapGroupIndexToValue.put(groupIndex, value);
		}
		public boolean isSuccess() {
			for (Integer groupIndex : this.mapGroupIndexToValue.keySet()) {
				if (this.mapGroupIndexToValue.get(groupIndex) == null) {
					return false;
				}
			}
			return true;
		}
	}
}
