package christo.ffxivbot;


import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import javax.security.auth.login.LoginException;

import org.json.JSONArray;
import org.json.JSONObject;

import christo.ffxivbot.dataBase.CharacterDB;
import christo.ffxivbot.ffxiv.CharacterCard;
import christo.ffxivbot.ffxiv.fc.FreeCompagnie;
import christo.ffxivbot.ffxiv.xivdbAPI.FFXIVCharacter;
import christo.ffxivbot.ffxiv.xivdbAPI.XIVBD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HelpfulSyplh extends ListenerAdapter {
    
	static JSONObject charDataBase =  new JSONObject();
	
    public static void main(String[] args) {
    	
    	charDataBase = CharacterDB.makeDB(charDataBase);
    	
        //We construct a builder for a BOT account. If we wanted to use a CLIENT account
        // we would use AccountType.CLIENT
        try {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken("xxxx")           //The token of the account that is logging in.
                    .addListener(new HelpfulSyplh())  //An instance of a class that will handle events.
                    .buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
        }catch (LoginException e) { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        catch (RateLimitedException e) { e.printStackTrace(); }
    }
    
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    	User usr = event.getMember().getUser();
    	usr.openPrivateChannel().queue();
    	PrivateChannel channel = usr.getPrivateChannel();
    	//channel.sendMessage("boop!").queue();
    }
    
    /**
     * NOTE THE @Override!
     * This method is actually overriding a method in the ListenerAdapter class! We place an @Override annotation
     *  right before any method that is overriding another to guarantee to ourselves that it is actually overriding
     *  a method from a super class properly. You should do this every time you override a method!
     *
     * As stated above, this method is overriding a hook method in the
     * {@link net.dv8tion.jda.core.hooks.ListenerAdapter ListenerAdapter} class. It has convience methods for all JDA events!
     * Consider looking through the events it offers if you plan to use the ListenerAdapter.
     *
     * In this example, when a message is received it is printed to the console.
     *
     * @param event
     *          An event containing information about a {@link net.dv8tion.jda.core.entities.Message Message} that was
     *          sent in a channel.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        //Event specific information
        User author = event.getAuthor();                  //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to. This could be a TextChannel, PrivateChannel, or Group!

        String msg = message.getContent();              //This returns a human readable version of the Message. 
        boolean bot = author.isBot(); 

        if (event.isFromType(ChannelType.TEXT)){ //If this message was sent to a Guild TextChannel
            Guild guild = event.getGuild();
            TextChannel textChannel = event.getTextChannel();
            Member member = event.getMember();
            String name = member.getEffectiveName();

            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
        } else if (event.isFromType(ChannelType.PRIVATE)) { //If this message was sent to a PrivateChannel 
            PrivateChannel privateChannel = event.getPrivateChannel();
        } else if (event.isFromType(ChannelType.GROUP)) {  //If this message was sent to a Group. This is CLIENT only!
            Group group = event.getGroup();
            String groupName = group.getName() != null ? group.getName() : "";
        }

        if (msg.equals("!ping")) {
            channel.sendMessage("pong!").queue();
        } else if (msg.equals("!roll")) {

        	Random rand = new Random();
            int roll = rand.nextInt(6) + 1; //This results in 1 - 6 (instead of 0 - 5)
            channel.sendMessage("Your roll: " + roll).queue(sentMessage ->  //This is called a lambda statement.
            { if (roll < 3) { channel.sendMessage("Your roll wasn't very good... Must be bad luck!\n").queue(); }});
        } else if (msg.startsWith("!searchDB")){
        	System.out.println("searching character...");
        	String[] msgParts = msg.split(" ");
        	if(msgParts.length>2){
	        	try {
	    			InputStream searchRep = new URL(XIVBD.SEARCH_URL+msgParts[1]+" "+msgParts[2]).openStream();
	    			try(Scanner scan = new Scanner(searchRep)){
	    				String searchBody = scan.nextLine();
	    				
	    				JSONArray searchRes = new JSONObject(searchBody).getJSONObject("characters").getJSONArray("results");
	    				
	    				List<Integer> ids = new ArrayList<>();
	    				
	    				for(int i = 0; i < searchRes.length(); i++){
	    					ids.add(searchRes.getJSONObject(i).getInt("id"));
	    				}
	    				
	    				searchRep.close();
	    				
	    				String mess = "You have requested a search on: `"+msgParts[1]+" "+msgParts[2]+"`";
	    				if(ids.size()>1) mess += ", there are "+ids.size()+" characters with this name.";
	    				if(msgParts.length>3) mess += " I will be showing the results for `"+msgParts[3]+"`";
	    				else if(ids.size()>5) mess += " I will be showing the 5 first results.";
	    				
	    				channel.sendMessage(mess).queue();
						boolean found = false;
	    				
	    				for(int i = 0; i < ids.size(); i++){
	    					
	    					FFXIVCharacter character = new FFXIVCharacter(ids.get(i), null);
	    					
	    					if(msgParts.length>3){
		    					if(character.server.equalsIgnoreCase(msgParts[3])) {
		    						channel.sendMessage(new CharacterCard(character).getCard()).queue();
		    						found = true;
		    					}
	    					}else{	  
	    						found = true;
	    						channel.sendMessage(new CharacterCard(character).getCard()).queue();
	    						if(i>4) break;
	    					}
	    				}	
	    				
	    				if(!found) channel.sendMessage("There is no one named `"+msgParts[1]+" "+msgParts[2]+"` on `"+msgParts[3]+"`").queue();
	    			}
	    		} catch(Exception e){ e.printStackTrace(); }
        	} else channel.sendMessage("Please enter both first and last names, with a space ex: ` !searchDB Tataru Taru` ").queue();
        } else if(msg.startsWith("!iam")){
        	System.out.println("registering character...");
        	String[] msgParts = msg.split(" ");
        	if(msgParts.length>3){
        		
				JSONObject fcMembers = FreeCompagnie.getMembers("9232238498621161992");
        		
				//fcMembers.ge
				
        		CharacterDB.addToDB(new FFXIVCharacter(XIVBD.getCharID(msgParts[1]+" "+msgParts[2], msgParts[3]), author.getId()), charDataBase);
        		
        		File dbFile = new File("characterDB.json");
        		try {
					
        			BufferedWriter bufWr = new BufferedWriter(new FileWriter(dbFile));
					bufWr.write(charDataBase.toString());
					
					bufWr.close();
					
				} catch (IOException e) { e.printStackTrace(); }
        		
        		
        		int rID = 10; //default to 11th role as lys...
				for(int i = 0; i < event.getGuild().getRoles().size(); i++)
					if(event.getGuild().getRoles().get(i).getName().equalsIgnoreCase("lys")) rID = i;
				
				channel.sendMessage("registered "+author.getAsMention()+" as `"+msgParts[1]+" "+msgParts[2]+"`").queue();
				GuildController gcon = event.getGuild().getController();
				gcon.setNickname(event.getMember(), msgParts[1]+" "+msgParts[2]).queue();
				
				gcon.addRolesToMember(event.getMember(), event.getGuild().getRoles().get(rID)).queue();
					
	        		
	        		
        	} else channel.sendMessage("Please enter both first and last names, with a space ex: ` !iam Tataru Taru Gilgamesh` ").queue();
        }
    }
}