package dev.sunnymonster.thedaemon.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheDaemonClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("the_daemon");

    // This is the IP that players needs to find.
    public static String hiddenIP = "";
    // This is the IP we redirect the player to.
    public static String redirectIP = "";

    @Override
    public void onInitializeClient() {
        LOGGER.info("THE DAEMON IS ACTIVE.");
    }
}
