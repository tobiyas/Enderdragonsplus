package de.tobiyas.enderdragonsplus.entity.dragon.controllers.properties;

import java.util.HashMap;
import java.util.Map;

import de.tobiyas.enderdragonsplus.entity.dragon.controllers.properties.NBTTagDragonStore.DragonNBTReturn;

public class PropertyController {
	
	private Map<String,Object> propertyMap;

	public PropertyController() {
		propertyMap = new HashMap<String, Object>();
	}
	
	public PropertyController(DragonNBTReturn returnContainer) {
		propertyMap = returnContainer.getProperties();
	}
	
	
	public void addProperty(String name, Object value){
//		try{
//			NBTTagCompound properties = propertyMap.getCompound("propertyList");
//			boolean setSomething = false;
//			
//			if(value instanceof String){
//				propertyMap.setString(name, (String) value);
//				properties.setString(name, (String) value);
//				setSomething = true;
//			}
//			
//			if(value instanceof Long){
//				propertyMap.setLong(name, (Long) value);
//				properties.setLong(name, (Long) value);
//				setSomething = true;
//			}
//			
//			if(value instanceof Boolean){
//				propertyMap.setBoolean(name, (Boolean) value);
//				properties.setBoolean(name, (Boolean) value);
//				setSomething = true;
//			}
//			
//			if(value instanceof Byte){
//				propertyMap.setByte(name, (Byte) value);
//				properties.setByte(name, (Byte) value);
//				setSomething = true;
//			}
//
//			if(value instanceof Double){
//				propertyMap.setDouble(name, (Double) value);
//				properties.setDouble(name, (Double) value);
//				setSomething = true;
//			}
//			
//			if(value instanceof Float){
//				propertyMap.setFloat(name, (Float) value);
//				properties.setFloat(name, (Float) value);
//				setSomething = true;
//			}
//			
//			if(value instanceof Integer){
//				propertyMap.setInt(name, (Integer) value);
//				properties.setInt(name, (Integer) value);
//				setSomething = true;
//			}
//			
//			if(value instanceof Short){
//				propertyMap.setShort(name, (Short) value);
//				properties.setShort(name, (Short) value);
//				setSomething = true;
//			}
//			
//			if(!setSomething){
//				String className = value.getClass().getCanonicalName();
//				throw new IllegalArgumentException("The given Class '" + className + "' is not supported by NBTTag.");
//			}
//			
//			propertyMap.set("propertyList", properties);
//		}catch(Exception exp){
//			String className = value.getClass().getCanonicalName();
//			throw new IllegalArgumentException("The given Class '" + className + "' is not supported by NBTTag.");
//		}
		propertyMap.put(name, value);
	}
	
	public void removeProperty(String name){
		propertyMap.remove(name);
	}
	
	public Object getProperty(String name){
		return propertyMap.get(name);
	}

	public Map<String,Object> getAllProperties() {
		return new HashMap<String, Object>(propertyMap);
	}
	
}
