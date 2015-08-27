/*
 * StopEnderdragonPortals - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.enderdragonsplus;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;

import de.tobiyas.enderdragonsplus.bridges.BridgeController;
import de.tobiyas.enderdragonsplus.commands.CommandEDP;
import de.tobiyas.enderdragonsplus.commands.CommandFlyTo;
import de.tobiyas.enderdragonsplus.commands.CommandGoHome;
import de.tobiyas.enderdragonsplus.commands.CommandInfo;
import de.tobiyas.enderdragonsplus.commands.CommandKillEnderDragon;
import de.tobiyas.enderdragonsplus.commands.CommandReloadConfig;
import de.tobiyas.enderdragonsplus.commands.CommandRespawner;
import de.tobiyas.enderdragonsplus.commands.CommandRide;
import de.tobiyas.enderdragonsplus.commands.CommandSpawnEnderDragon;
import de.tobiyas.enderdragonsplus.configuration.Config;
import de.tobiyas.enderdragonsplus.configuration.ConfigTemplate;
import de.tobiyas.enderdragonsplus.damagewhisperer.EntityDamageWhisperController;
import de.tobiyas.enderdragonsplus.datacontainer.Container;
import de.tobiyas.enderdragonsplus.datacontainer.DragonLogicTicker;
import de.tobiyas.enderdragonsplus.datacontainer.OnTheFlyReplacer;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragonVersionManager;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainer;
import de.tobiyas.enderdragonsplus.entity.dragon.age.AgeContainerManager;
import de.tobiyas.enderdragonsplus.entity.dragon.v1_7_2.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.listeners.Listener_DragonRiderTeleport;
import de.tobiyas.enderdragonsplus.listeners.Listener_Dragon_Spawn;
import de.tobiyas.enderdragonsplus.listeners.Listener_Entity;
import de.tobiyas.enderdragonsplus.listeners.Listener_Fireball;
import de.tobiyas.enderdragonsplus.listeners.Listener_Plugins;
import de.tobiyas.enderdragonsplus.listeners.Listener_Sign;
import de.tobiyas.enderdragonsplus.listeners.Listener_World;
import de.tobiyas.enderdragonsplus.meshing.MeshManager;
import de.tobiyas.enderdragonsplus.spawner.DragonSpawnerManager;
import de.tobiyas.enderdragonsplus.util.Consts;
import de.tobiyas.util.UtilsUsingPlugin;
import de.tobiyas.util.metrics.SendMetrics;


public class EnderdragonsPlus extends UtilsUsingPlugin{
	
	private Config config;
	
	private Container container;
	
	private AgeContainerManager ageContainerManager;
	
	private BridgeController bridgeController;
	private DragonSpawnerManager dragonSpawnerManager;
	
	private EntityDamageWhisperController damageWhisperController;
	
	private static EnderdragonsPlus plugin;

	/**
	 * The meshManager to use.
	 */
	private MeshManager meshManager;
	
	
	@Override
	public void pluginEnable(){
		plugin = this;
		
		if(!tryInjectDragon()) {
			log("Could not inject Dragon. Disabling Plugin.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		meshManager = new MeshManager();
		meshManager.init();
		
		setupConfiguration();
		setupAgeContainer();
		checkAgeContainerSynthax();
		container = new Container();
		
		checkDepends();
		registerEvents();
		registerCommands();
		
		registerTasks();
		
		registerManagers();
		initMetrics();
		
		log(getDescription().getFullName() + " fully loaded with: " + getPermissionManager().getPermissionsName());
	}
	
	

	//The Consts the EnderDragon is Identified With
	private final int edInt = 63; //63 is ED
	private final String edName = "LimitedEnderDragon";
	private final Class<?> edClass = LimitedEnderDragonVersionManager.getCurrentClass();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean tryInjectDragon(){
		try{
			Class<?> entityTypeClass = Class.forName("net.minecraft.server.v" + getRelocationAddition() + ".EntityTypes");
			if(getRelocationAddition().equals("1_7_R1") || getRelocationAddition().equalsIgnoreCase("1_7_R2") 
					|| getRelocationAddition().equalsIgnoreCase("1_7_R3")
					|| getRelocationAddition().equalsIgnoreCase("1_8_R1")
					|| getRelocationAddition().equalsIgnoreCase("1_8_R2")
					|| getRelocationAddition().equalsIgnoreCase("1_8_R3")){
				
				Field c = entityTypeClass.getDeclaredField("c"); c.setAccessible(true);
				HashMap c_map = (HashMap) c.get(null);
				c_map.put(edName, edClass);
				
				Field d = entityTypeClass.getDeclaredField("d"); d.setAccessible(true);
				HashMap d_map = (HashMap) d.get(null);
				d_map.put(edClass, edName);
				
				Field e = entityTypeClass.getDeclaredField("e"); e.setAccessible(true);
				HashMap e_map = (HashMap) e.get(null);
				e_map.put(edInt, edClass);
				
				Field f = entityTypeClass.getDeclaredField("f"); f.setAccessible(true);
				HashMap f_map = (HashMap) f.get(null);
				f_map.put(edClass, edInt);
				
				Field g = entityTypeClass.getDeclaredField("g"); g.setAccessible(true);
				HashMap g_map = (HashMap) g.get(null);
				g_map.put(edName, edInt);
				
				return true;	
			}
			
			
			if(getRelocationAddition().equals("1_6_R1") || getRelocationAddition().equals("1_6_R2")
					|| getRelocationAddition().equals("1_6_R3")){
		        Method method = entityTypeClass.getDeclaredMethod("a", new Class[] { Class.class, String.class, Integer.TYPE });
		        method.setAccessible(true);
		      
		        method.invoke(
		    		  entityTypeClass, new Object[] { 
		    	  		LimitedEnderDragon.class, 
		    	  		edName, 
		    	  		edInt,
	    	  	});
		        
		        return true;
			}
		

			return false;
	    } catch (NoClassDefFoundError exp) {
	    	log("Could not inject LimitedEnderDragon. Disabling Plugin.");
	    	log("You are probably using the wrong Version. Your version: " + Bukkit.getVersion() + " supportet EnderDragonsPlusVersion: " + Consts.SupportetVersion);
	    	exp.printStackTrace();
	    	Bukkit.getPluginManager().disablePlugin(this);
	    	return false;
	    } catch (Throwable exp) {
			log("Something has gone wrong while injekting! Plugin will be disabled!");
			exp.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return false;
		}
	}
	
	private void checkDepends(){
		bridgeController = new BridgeController();
	}
	
	@Override
	public void onDisable(){
		try{
			dragonSpawnerManager.saveList();
		}catch(Throwable exp){}
		
		log("disabled "+getDescription().getFullName());

	}


	private void registerEvents(){
		new Listener_Entity();
		new Listener_World();
		new Listener_Plugins();
		new Listener_Sign();
		new Listener_Fireball();
		new Listener_Dragon_Spawn();
		new Listener_DragonRiderTeleport();
	}
	
	private void registerCommands(){
		new CommandKillEnderDragon();
		new CommandSpawnEnderDragon();
		new CommandReloadConfig();
		new CommandGoHome();
		new CommandInfo();
		new CommandRespawner();
		new CommandEDP();
		new CommandRide();
		new CommandFlyTo(this);
		//new CommandFireBreath();
		//new CommandError();
		//new CommandDEBUGGOTO();
	}
	
	private void registerTasks(){
		new DragonLogicTicker();
		new OnTheFlyReplacer();
	}
	
	private void registerManagers(){
		dragonSpawnerManager = new DragonSpawnerManager();
		dragonSpawnerManager.init();
		
		damageWhisperController = new EntityDamageWhisperController();
	}


	private void setupConfiguration(){
		config = new Config(this);
		ConfigTemplate template = new ConfigTemplate();
		if(template.isOldConfigVersion()){
			template.writeTemplate();
		}
	}
	
	private void checkAgeContainerSynthax(){
		for(String ageName : ageContainerManager.getAllIncorrectAgeNames()){
			List<String> notCorrectFields = AgeContainer.getIncorrectFieldsOfAge(ageName);
			String ageIncorrectFieldString = "Age: " + ageName + " has incorrect fields: ";
			for(String incorrectField : notCorrectFields){
				ageIncorrectFieldString += incorrectField + ",";
			}
			
			logError(ageIncorrectFieldString);
			log(ageIncorrectFieldString);
		}
		
		Set<String> correctAges = ageContainerManager.getAllAgeNames();
		String correctAgeString = "Ages loaded:";
		for(String correctAge : correctAges){
			correctAgeString += " " + correctAge + ",";
		}
		
		if(correctAges.size() > 0){
			correctAgeString = correctAgeString.substring(0, correctAgeString.length() - 1);
			correctAgeString += ".";
			
			log(correctAgeString);
		}
		
	}
	
	private void setupAgeContainer(){
		ageContainerManager = new AgeContainerManager();
		ageContainerManager.reload();
	}
	
	private void initMetrics(){
		if(interactConfig().getConfig_uploadMetrics())
			SendMetrics.sendMetrics(this, false);
		
		boolean enableErrorReport = interactConfig().getConfig_uploadErrors();
		getDebugLogger().enableUploads(enableErrorReport);
	}

	
	public Config interactConfig(){
		return config;
	}
	
	public static EnderdragonsPlus getPlugin(){
		return plugin;
	}
	
	public Container getContainer(){
		return container;
	}
	
	public BridgeController interactBridgeController(){
		return bridgeController;
	}
	
	public DragonSpawnerManager getDragonSpawnerManager(){
		return dragonSpawnerManager;
	}

	
	public EntityDamageWhisperController getDamageWhisperController(){
		return damageWhisperController;
	}
	
	public AgeContainerManager getAgeContainerManager(){
		return ageContainerManager;
	}
	
	public MeshManager getMeshManager() {
		return meshManager;
	}

	/**
	 * The relocation addition for the classes.
	 */
	private static String relocationAddition;
	
	/**
	 * Gets the rev used.
	 * @return
	 */
	public static String getRelocationAddition(){
		if(relocationAddition != null) return relocationAddition;
		
		for(int main = 0; main < 10; main++){
			for(int sub = 0; sub < 20; sub++){
				for(int rev = 0; rev < 20; rev++){
					try{
						String addition = main + "_" + sub + "_R" + rev;
						Class.forName("net.minecraft.server.v" + addition +  ".Entity");
						relocationAddition = addition;
						return relocationAddition;
					}catch(Throwable exp){}
				}
			}
		}
		
		return null;
	}

}
