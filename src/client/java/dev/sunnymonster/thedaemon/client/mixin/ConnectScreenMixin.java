package dev.sunnymonster.thedaemon.client.mixin;

import dev.sunnymonster.thedaemon.client.TheDaemonClient;
import net.minecraft.client.gui.screens.ConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// This injects code into the server loading screen.
// This seems to also be the screen that is responsible for logging the server IP to the console.
@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

    // This hijacks the `connect` method, who tries to log the server IP to the console.
    @ModifyArg(method = "connect", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), index = 1)
    private Object hijackIP(Object o) {
        // We know for sure that the argument is a string.
        String ipString = (String) o;

        // If the client wants to log the redirect IP to the console, (the ugly IP we don't want to see)
        // We simply override it and log our fancy IP instead.
        if (ipString.equals(TheDaemonClient.redirectIP)) {
            return TheDaemonClient.hiddenIP;
        }

        // Otherwise just log whatever the client wants to, again, we don't want to interfere with the
        // player's normal playing.
        return o;
    }

}
