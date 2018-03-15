package net.minecrell.serverlistplus.sponge;

import lombok.RequiredArgsConstructor;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanDetector;
import org.spongepowered.api.Game;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;

@RequiredArgsConstructor
public class SpongeBanDetector implements BanDetector {

    private final Game game;

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        if (game.getServiceManager().provide(BanService.class).isPresent()) {
            final GameProfile profile = GameProfile.of(playerIdentity.getUuid(), playerIdentity.getName());
            return game.getServiceManager().provide(BanService.class).get().isBanned(profile);
        }

        return false;
    }

}
