package com.thoxia.playerads.ad;

import com.thoxia.playerads.PlayerAdsPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public interface AdManager {

    void init();

    /**
     * Has no impact on single instance setups
     * @return Cached value of AdManager#getAds, cache updates every 3 minutes and on every update
     */
    Collection<Ad> getCachedAds();

    void clearCache();

    boolean addToCache(Ad ad);

    boolean removeFromCache(Ad ad);

    CompletableFuture<Collection<Ad>> getAds();

    Collection<AdSpot> getSpots();

    AdSpot getSpot(int slot);

    CompletableFuture<Ad> getAd(AdSpot spot);

    default CompletableFuture<Ad> getAd(int slot) {
        return getAd(this.getSpot(slot));
    }

    CompletableFuture<Collection<Ad>> getAds(String player);

    void addAd(Ad ad);

    /**
     * Also adds the ad to data storage
     * @param ad Advertisement to add and post
     */
    void postAd(Ad ad, boolean postWebhook);

    CompletableFuture<Boolean> removeAds(Ad... ads);

    default CompletableFuture<Boolean> removeAds(String player) {
        return getAds(player)
                .exceptionally(throwable -> {
                    PlayerAdsPlugin.getInstance().getLogger().log(Level.SEVERE,
                            "An exception was found whilst fetching ads!", throwable);
                    return new ArrayList<>();
                })
                .thenCompose(ads -> removeAds(ads.toArray(new Ad[0])));
    }

    default CompletableFuture<Boolean> removeAd(Ad ad) {
        return removeAds(ad);
    }

    CompletableFuture<Long> getLastAdvertisementTime();

}
