package fr.tsadeo.app.dsntotree.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
			
			int count =  matcher.groupCount();
			
			capturingGroups.getGroups().stream()
			   .filter(group -> count >= group.intValue())
			   .forEach(group -> capturingGroups.setValue(group,  matcher.group(group)) );
			
			
		}
	}
	public  String[] extractsGroups(String value, Pattern pattern, int... groupIndexes) {
		Matcher matcher = getMatcher(value, pattern);
		String[] groups = null;
		
		if (matcher != null && matcher.matches()) {
			int count =  matcher.groupCount();
			groups =
			IntStream.of(groupIndexes)
			    .filter(index -> count >= index)
			    .mapToObj(index ->  matcher.group(index))
			    .toArray(size -> new String[size]);
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
		
		private final Map<Integer, String> mapGroupIndexToValue; 
		
		public CapturingGroups(Integer... groupIndexes) {
			if (groupIndexes != null) {
				
				mapGroupIndexToValue = Stream.of(groupIndexes)
				   .collect(Collectors.toMap(index -> index, index -> ""));
				
			} else {
				mapGroupIndexToValue = new HashMap<>(0);
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
			
			return this.mapGroupIndexToValue.keySet().stream()
			   .allMatch(groupIndex -> !this.mapGroupIndexToValue.get(groupIndex).isEmpty() );
			
		}
	}
}
