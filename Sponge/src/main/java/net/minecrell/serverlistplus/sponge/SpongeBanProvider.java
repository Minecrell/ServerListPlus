package net.minecrell.serverlistplus.sponge;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.ban.Ban;

import java.util.Date;
import java.util.Optional;

public class SpongeBanProvider implements BanProvider {

    private static Optional<BanService> getBanService() {
        return Sponge.getGame().getServiceManager().provide(BanService.class);
    }

    private static GameProfile getGameProfile(PlayerIdentity playerIdentity) {
        return GameProfile.of(playerIdentity.getUuid(), playerIdentity.getName());
    }

    private static Optional<Ban.Profile> getBan(PlayerIdentity playerIdentity) {
        final GameProfile profile = getGameProfile(playerIdentity);

        return getBanService().flatMap(banService -> banService.getBanFor(profile));
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        final GameProfile profile = getGameProfile(playerIdentity);

        return getBanService().map(banService -> banService.isBanned(profile)).orElse(false);
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        return getBan(playerIdentity).flatMap(Ban.Profile::getReason).map(TextSerializers.FORMATTING_CODE::serialize).orElse(null);
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        return getBan(playerIdentity).flatMap(Ban.Profile::getBanSource).map(TextSerializers.FORMATTING_CODE::serialize).orElse(null);
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        return getBan(playerIdentity).flatMap(Ban.Profile::getExpirationDate).map(Date::from).orElse(null);
    }

}
