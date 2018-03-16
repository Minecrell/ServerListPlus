package net.minecrell.serverlistplus.sponge;

import net.minecrell.serverlistplus.core.player.PlayerIdentity;
import net.minecrell.serverlistplus.core.player.ban.BanProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.ban.Ban;

import java.util.Date;

public class SpongeBanDetector implements BanProvider {

    private BanService getBanService() {
        return Sponge.getGame().getServiceManager().provide(BanService.class).orElse(null);
    }

    private GameProfile getGameProfile(PlayerIdentity playerIdentity) {
        return GameProfile.of(playerIdentity.getUuid(), playerIdentity.getName());
    }

    private Ban.Profile getBan(PlayerIdentity playerIdentity) {
        final BanService banService = getBanService();

        if (banService == null)
            return null;

        final GameProfile profile = getGameProfile(playerIdentity);

        return banService.getBanFor(profile).orElse(null);
    }

    @Override
    public boolean isBanned(PlayerIdentity playerIdentity) {
        final BanService banService = getBanService();

        if (banService == null)
            return false;

        final GameProfile profile = getGameProfile(playerIdentity);

        return banService.isBanned(profile);
    }

    @Override
    public String getBanReason(PlayerIdentity playerIdentity) {
        final Ban.Profile ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getReason().map(TextSerializers.FORMATTING_CODE::serialize).orElse(null);
    }

    @Override
    public String getBanOperator(PlayerIdentity playerIdentity) {
        final Ban.Profile ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getBanSource().map(TextSerializers.FORMATTING_CODE::serialize).orElse(null);
    }

    @Override
    public Date getBanExpiration(PlayerIdentity playerIdentity) {
        final Ban.Profile ban = getBan(playerIdentity);

        if (ban == null)
            return null;

        return ban.getExpirationDate().map(Date::from).orElse(null);
    }

}
