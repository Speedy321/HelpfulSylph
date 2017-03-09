package christo.ffxivbot.ffxiv.xivdbAPI;

public class ClassJob {
	
	public String name;
	public String abbr;
	
	public long xpGained; //total
	public long xpMax; //total
	public int level;
	
	public ClassJob(String name, int level) {
		this.name = name;
		this.level = level;
	}
}
