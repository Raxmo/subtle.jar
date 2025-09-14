package io.liska.subtle;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod(value = SubtleMod.MODID)
public class TimeTweekSaveData extends SavedData
{
    // [--------- Parameters ----------]

    // [---------- Variables ----------]
    private long nexttrigger;

    // [---------- Required methods ----------]
    public TimeTweekSaveData()
    {
        NeoForge.EVENT_BUS.register(this);
        nexttrigger = 0;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries)
    {
        tag.putLong("nexttrigger", nexttrigger);
        return tag;
    }

    private static TimeTweekSaveData _retrieve_(CompoundTag tag, net.minecraft.core.HolderLookup.Provider lookupProvider) {
        TimeTweekSaveData data = new TimeTweekSaveData();

        for(String key : tag.getAllKeys())
        {
            switch(key)
            {
                case "nexttrigger" ->
                {
                    data.nexttrigger = tag.getLong(key);
                }
                default -> {}
            }
        }
        return data;
    }

    private ServerLevel clevel;

    public static TimeTweekSaveData load(MinecraftServer server)
    {
        ServerLevel level = server.overworld();
        DimensionDataStorage storage = level.getDataStorage();
        Factory<TimeTweekSaveData> factory = new Factory<>(TimeTweekSaveData::new, TimeTweekSaveData::_retrieve_);

        TimeTweekSaveData TTSD = storage.computeIfAbsent(factory, "TimeTweekSaveData");
        TTSD.clevel = level;

        return TTSD;
    }

    // [---------- My methods ----------]
    private double Koi (double start, double end, double slope, double t)
    {
        return ((start - end) / (((-t * slope) / (start - end)) + 1)) + end;
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event)
    {
        if(clevel == null) return;
        if(nexttrigger > clevel.getGameTime()) return;

        // ======================================================== //
        double t = clevel.getDayTime();

        double lower = Koi(3000, 0, -0.25, t);
        double upper = Koi(9000, 10, -0.25, t);
        double amountb = Koi(0, 2400, 0.1, t);

        long amount1 = (long)((Math.random() * Math.random() * amountb));
        long amount2 = (long)((Math.random() * Math.random() * amountb));

        long jump = amount1 - amount2;
        long cooldown = (long)(Math.random() * (upper - lower) + lower);

        clevel.setDayTime((long)Math.max(0, t + jump));
        nexttrigger = clevel.getGameTime() + cooldown;

        SubtleMod.LOGGER.info(">>>>    [cooldow] {}, [jump] {}    <<<<", cooldown, jump);

        setDirty();
    }
}
