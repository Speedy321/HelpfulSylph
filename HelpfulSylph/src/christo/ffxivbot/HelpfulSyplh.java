package christo.ffxivbot;


import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

import javax.security.auth.login.LoginException;

import org.json.JSONArray;
import org.json.JSONObject;

import christo.ffxivbot.ffxiv.xivdbAPI.FFXIVCharacter;
import christo.ffxivbot.ffxiv.xivdbAPI.XIVBD;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HelpfulSyplh extends ListenerAdapter {
    /**
     * This is the method where the program starts.
     */
    public static void main(String[] args) {
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
	    				if(ids.size()>10) mess += " I will be showing the 10 first results.";
	    				
	    				for(int i = 0; (i < ids.size())&&(i < 10); i++){
	    					
	    					FFXIVCharacter character = new FFXIVCharacter(ids.get(i));
	
	    					mess += "\n"+
	    							"```"+character.name+" is a "+character.gender+" "+character.race+
	    							" who is playing on "+character.server+" for the "+character.grandCompany+
	    							" and has reached the rank of "+character.gcRank+"```";
	    				}

	    				channel.sendMessage(mess).queue();
	    				
	    			}
	    		} catch(Exception e){ e.printStackTrace(); }
        	} else {
        		channel.sendMessage("Please enter both first and last names, with a space ex: ` !searchDB Tataru Taru` ").queue();
        	}
        }
    }
}