package de.tobiyas.enderdragonsplus.entity.dragon.age;

import java.util.List;

public class AgeFieldIncorrectException extends AgeNotFoundException {
	private static final long serialVersionUID = 1L;
	
	private List<String> fieldNames;
	
	public AgeFieldIncorrectException(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}
	
	public List<String> getFieldNames(){
		return fieldNames;
	}
}
