package dev.sunnymonster.thedaemon.client.mixin;

import dev.sunnymonster.thedaemon.client.AES;
import dev.sunnymonster.thedaemon.client.TheDaemonClient;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;

// This mixin injects code to the multiplayer server list screen
@Mixin(JoinMultiplayerScreen.class)
abstract class JoinMultiplayerScreenMixin {

    // This hijacks the `join` function under the multiplayer screen to modify
    // the ip when it matches.
    @ModifyArg(method = "join", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ConnectScreen;startConnecting(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/multiplayer/resolver/ServerAddress;Lnet/minecraft/client/multiplayer/ServerData;ZLnet/minecraft/client/multiplayer/TransferState;)V"), index = 2)
    private ServerAddress hijackIP(ServerAddress originalAddress) throws NoSuchAlgorithmException {
        // Hash the IP the players just inputted.
        // It is statistically impractical to reverse engineer the correct IP from this hash,
        // but once the player does find the right IP, the hash will match.
        // This ensures that I don't know the IP either so that I can contribute without spoiling it for myself.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encoded = digest.digest(originalAddress.getHost().getBytes(StandardCharsets.UTF_8));
        byte[] target = HexFormat.of().parseHex("f8cdc862d88c975ff062e7b2fe20279d5c071191778d9138af28562d9fba8f51");

        // If the hash matches
        if (Arrays.equals(encoded, target)) {
            TheDaemonClient.LOGGER.info("HOLY SHIT");

            // Remember the IP players are trying to find.
            // This is useful later when we also hijack the console logging of joining the server.
            // (See `ConnectScreenMixin` for more info)
            // No one wants to see the ugly IP we are redirecting to. Immersion FTW!
            TheDaemonClient.hiddenIP = originalAddress.getHost();

            // Similarly, the new IP is also encrypted, and it has to be decrypted using the IP players are trying to find.
            // This means without the previous step it is also not possible to get from this hash to the redirecting IP.
            // So that you cannot skip this step. You are trapped.
            String newIP = AES.decrypt("zUqCncFvYAqyDNq/H79sx+w0xvcza9Pk7GSY6v45CHxlQrIo+WsHTHW6f9nVLQWY", originalAddress.getHost());
            assert newIP != null;

            // Again remember the redirect IP for later when we hijack the logging.
            TheDaemonClient.redirectIP = newIP;

            return new ServerAddress(newIP, 25565);
        }

        // If the hash does not match, we simply let our players join the server they wanted to join.
        // We don't want to interfere with their normal playing.
        return originalAddress;
    }

}
