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

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Random;

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
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
                                                        //  This could be a TextChannel, PrivateChannel, or Group!

        String msg = message.getContent();              //This returns a human readable version of the Message. Similar to
                                                        // what you would see in the client.

        boolean bot = author.isBot();                     //This boolean is useful to determine if the User that
                                                        // sent the Message is a BOT or not!

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
            {                                                              
                if (roll < 3)
                {
                    channel.sendMessage("Your roll wasn't very good... Must be bad luck!\n").queue();
                }
            });
        } else if (msg.startsWith("!kick")) {   //Note, I used "startsWith, not equals.
            //This is an admin command. It needs Permission.KICK_MEMBERS. 
            //We only want to deal with message sent in a Guild.
            if (message.isFromType(ChannelType.TEXT)) {
                if (message.getMentionedUsers().isEmpty()) {
                    channel.sendMessage("You must mention 1 or more Users to be kicked!").queue();
                }else {
                    Guild guild = event.getGuild();
                    Member selfMember = guild.getSelfMember();  //This is the currently logged in account's Member object.
                    
                    if (!selfMember.hasPermission(Permission.KICK_MEMBERS)) {
                        channel.sendMessage("Sorry! I don't have permission to kick members in this Guild!").queue();
                        return; //We jump out of the method instead of using cascading if/else
                    }
                    //Loop over all mentioned users, kicking them one at a time. Mwauahahah!
                    List<User> mentionedUsers = message.getMentionedUsers();
                    for (User user : mentionedUsers) {
                        Member member = guild.getMember(user);  //We get the member object for each mentioned user to kick them!

                        //We need to make sure that we can interact with them. Interacting with a Member means you are higher
                        // in the Role hierarchy than they are. Remember, NO ONE is above the Guild's Owner. (Guild#getOwner())
                        if (!selfMember.canInteract(member))
                        {
                            channel.sendMessage("Cannot kicked member: " + member.getEffectiveName() +", they are higher " +
                                    "in the hierachy than I am!").queue();
                            continue;   //Continue to the next mentioned user to be kicked.
                        }

                        //Remember, due to the fact that we're using queue we will never have to deal with RateLimits.
                        // JDA will do it all for you so long as you are using queue!
                        guild.getController().kick(member).queue(
                            success -> channel.sendMessage("Kicked " + member.getEffectiveName() + "! Cya!").queue(),
                            error ->
                            {
                                //The failure consumer provides a throwable. In this case we want to check for a PermissionException.
                                if (error instanceof PermissionException)
                                {
                                    PermissionException pe = (PermissionException) error;
                                    Permission missingPermission = pe.getPermission();  //If you want to know exactly what permission is missing, this is how.
                                                                                        //Note: some PermissionExceptions have no permission provided, only an error message!

                                    channel.sendMessage("PermissionError kicking [" + member.getEffectiveName()
                                            + "]: " + error.getMessage()).queue();
                                }
                                else
                                {
                                    channel.sendMessage("Unknown error while kicking [" + member.getEffectiveName()
                                            + "]: " + "<" + error.getClass().getSimpleName() + ">: " + error.getMessage()).queue();
                                }
                            });
                    }
                }
            }
            else
            {
                channel.sendMessage("This is a Guild-Only command!").queue();
            }
        }
    }
}