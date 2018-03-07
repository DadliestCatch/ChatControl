package ChatControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;

import ChatControl.Main;

public class Config {
	static Properties prop = new Properties();
    static OutputStream output = null;
    public static File config = new File("mods/ChatControl/config.properties");

    public static boolean readConfig() {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            try {
                input = new FileInputStream(config);
            } catch (FileNotFoundException e) {
                createConfig();
                return false;
            }

            prop.load(input);
            
            Main.adminBypass = Boolean.parseBoolean(prop.getProperty("admin-bypass"));
            Main.chatCooldown = Long.parseLong(prop.getProperty("chat-cooldown"));
            Main.advertFlags = Arrays.asList(prop.getProperty("advertisement-flags").split(","));
            Main.curses = Arrays.asList(prop.getProperty("curses").split(","));
            Main.doCensor = Boolean.parseBoolean(prop.getProperty("censor"));
            Main.doAdvertCheck = Boolean.parseBoolean(prop.getProperty("advert-check"));
            Main.doSpamCheck = Boolean.parseBoolean(prop.getProperty("spam-check"));
            Main.censorsToSuspension = Integer.parseInt(prop.getProperty("censors-to-suspension"));
            Main.adsToSuspension = Integer.parseInt(prop.getProperty("adverts-to-suspension"));
            Main.spamToSuspension = Integer.parseInt(prop.getProperty("spam-to-suspension"));

        } catch (IOException ex) {
            System.out.println("[ChatControl/WARN]: Disabled! Configuration error. " + ex.getMessage());
        }
        try {
            input.close();
        } catch (IOException e) {
        	System.out.println("[ChatControl/WARN]: Disabled! Configuration error. " + e.getMessage());
            return false;
        }
        System.out.println("[ChatControl/INFO]: Config: OK");
        return true;
    }

    public static void createConfig() {
        try {
            config.getParentFile().mkdirs();

            output = new FileOutputStream(config);

            prop.setProperty("admin-bypass", "false");
            prop.setProperty("chat-cooldown", "2500");
            prop.setProperty("advertisement-flags", "mc.,play.,tekkit.,server.,my server,join:,:2,free op,pvp.");
            prop.setProperty("curses", "anal,anus,arse,ass,ballsack,balls,bastard,bitch,biatch,bloody,blowjob,blow job,bollock,bollok,boner,boob,bugger,bum,butt,buttplug,clitoris,cock,coon,crap,cunt,damn,dick,dildo,dyke,fag,feck,fellate,fellatio,felching,fuck,f u c k,fudgepacker,fudge packer,flange,Goddamn,God damn,hell,homo,jerk,jizz,knobend,knob end,labia,lmao,lmfao,muff,nigger,nigga,omg,penis,piss,poop,prick,pube,pussy,queer,scrotum,sex,shit,s hit,sh1t,slut,smegma,spunk,tit,tosser,turd,twat,vagina,wank,whore,wtf");
            prop.setProperty("censor", "true");
            prop.setProperty("advert-check", "true");
            prop.setProperty("spam-check", "true");
            prop.setProperty("censors-to-suspension", "3");
            prop.setProperty("adverts-to-suspension", "1");
            prop.setProperty("spam-to-suspension", "3");

            prop.store(output, "ChatControl Configuration");

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                    System.out.println("[ChatControl/INFO]: Configuration file created.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
