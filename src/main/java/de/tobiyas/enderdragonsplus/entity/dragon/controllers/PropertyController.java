package de.tobiyas.enderdragonsplus.entity.dragon.controllers;

import javax.naming.OperationNotSupportedException;

import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTTagList;
import net.minecraft.server.v1_6_R3.NBTTagString;

import de.tobiyas.enderdragonsplus.entity.dragon.controllers.NBTTagDragonStore.DragonNBTReturn;

public class PropertyController {
	
	private NBTTagCompound propertyMap;

	public PropertyController() {
		propertyMap = new NBTTagCompound();
	}
	
	public PropertyController(DragonNBTReturn returnContainer) {
		propertyMap = returnContainer.getProperties();
	}
	
	public void addProperty(String name, Object value) throws OperationNotSupportedException{
		try{
			NBTTagList properties = propertyMap.getList("propertyList");
			properties.add(new NBTTagString(name));
			
			boolean setSomething = false;
			
			if(value instanceof String){
				propertyMap.setString(name, (String) value);
				setSomething = true;
			}
			
			if(value instanceof Long){
				propertyMap.setLong(name, (Long) value);
				setSomething = true;
			}
			
			if(value instanceof Boolean){
				propertyMap.setBoolean(name, (Boolean) value);
				setSomething = true;
			}
			
			if(value instanceof Byte){
				propertyMap.setByte(name, (Byte) value);
				setSomething = true;
			}

			if(value instanceof Double){
				propertyMap.setDouble(name, (Double) value);
				setSomething = true;
			}
			
			if(value instanceof Float){
				propertyMap.setFloat(name, (Float) value);
				setSomething = true;
			}
			
			if(value instanceof Integer){
				propertyMap.setInt(name, (Integer) value);
				setSomething = true;
			}
			
			if(value instanceof Short){
				propertyMap.setShort(name, (Short) value);
				setSomething = true;
			}
			
			if(!setSomething){
				String className = value.getClass().getCanonicalName();
				throw new OperationNotSupportedException("The given Class '" + className + "' is not supported by NBTTag.");
			}
			
			propertyMap.set("propertyList", properties);
		}catch(Exception exp){
			String className = value.getClass().getCanonicalName();
			throw new OperationNotSupportedException("The given Class '" + className + "' is not supported by NBTTag.");
		}
	}
	
	public void removeProperty(String name){
		
	}
	
	public Object getProperty(String name){
		return null;
	}

	public NBTTagCompound getAllProperties() {
		return propertyMap; //TODO test, disabled because not needed.
	}
	
}
