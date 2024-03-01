package net.mrliam2614.mrliamapi.spigot.messages;

public class Logger {
    public static void info(String message){
        info("MrLiamAPI", message);
    }
    public static void error(String message){
        error("MrLiamAPI", message);
    }

    public static void info(String owner, String message){
        System.Logger logger = System.getLogger(owner);
        logger.log(System.Logger.Level.INFO, message);
    }
    public static void error(String owner, String message){
        System.Logger logger = System.getLogger(owner);
        logger.log(System.Logger.Level.ERROR, message);
    }
}
