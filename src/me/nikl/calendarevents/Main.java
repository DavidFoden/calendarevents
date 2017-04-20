package me.nikl.calendarevents;

import me.nikl.calendarevents.nms.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;

/**
 * Main class
 */
public class Main extends JavaPlugin{
	private NMSUtil nms;
	private Timer timer;
	private EventsManager eventsManager;
	static boolean debug = false;
	
	private File sta, con;
	private FileConfiguration stats, config;

	private APICalendarEvents api;
	
	
	@Override
	public void onEnable(){
		reload();
		setUpNMS();
		this.eventsManager = new EventsManager(this);
		this.timer = new Timer(this);

		this.api = new APICalendarEvents(eventsManager);
	}
	
	@Override
	public void onDisable(){
		this.timer.cancel();
	}
	
	private void reload() {
		if(this.con == null)this.con = new File(this.getDataFolder().toString() + File.separatorChar + "config.yml");
		if(this.sta == null)this.sta = new File(this.getDataFolder().toString() + File.separatorChar + "data.yml");
		if (!con.exists()) {
			this.saveResource("config.yml", false);
		}
		if (!sta.exists()) {
			try {
				sta.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// reload configuration
		try {
			this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(this.con), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		InputStream defConfigStream = this.getResource("config.yml");
		if (defConfigStream != null){
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.config.setDefaults(defConfig);
		}
		
		// if this method was not called from onEnable stats is not null and has to be saved to the file first!
		if (stats != null) {
			try {
				this.stats.save(sta);
			} catch (IOException e) {
				getLogger().log(Level.SEVERE, "Could not save statistics", e);
			}
		}
		
		// load data file
		try {
			this.stats = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(this.sta), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	private boolean setUpNMS() {
		String version;
		
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		
		if(debug) getLogger().info("Your server is running version " + version);
		
		switch (version) {
			case "v1_10_R1":
				nms = new NMSUtil_1_10_R1();
				
				break;
			case "v1_9_R2":
				nms = new NMSUtil_1_9_R2();
				
				break;
			case "v1_9_R1":
				nms = new NMSUtil_1_9_R1();
				
				break;
			case "v1_8_R3":
				nms = new NMSUtil_1_8_R3();
				
				break;
			case "v1_8_R2":
				nms = new NMSUtil_1_8_R2();
				
				break;
			case "v1_8_R1":
				nms = new NMSUtil_1_8_R1();
				
				break;
			case "v1_11_R1":
				nms = new NMSUtil_1_11_R1();
				
				break;
		}
		return nms != null;
	}
	
	NMSUtil getNms(){
		return this.nms;
	}
	
	EventsManager getEventsManager(){
		return this.eventsManager;
	}
	
	void getNewTimer() {
		this.timer = new Timer(this);
	}

	/**
	 * Get the API instance to manipulate Events on runtime.
	 *
	 * @return API instance
	 */
	public APICalendarEvents getApi(){
		return this.api;
	}
}
