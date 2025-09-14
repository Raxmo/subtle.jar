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
    // [---------- Variables ----------]
    private long nexttrigger;
    private long cooldown;

    // [---------- Required methods ----------]
    public TimeTweekSaveData()
    {
        NeoForge.EVENT_BUS.register(this);
        nexttrigger = 100;
        cooldown = 100;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries)
    {
        tag.putLong("nexttrigger", nexttrigger);
        tag.putLong("cooldown", cooldown);
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
                case "cooldown" ->
                {
                    data.cooldown = tag.getLong(key);
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

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event)
    {
        if(clevel == null) return;
        if(clevel.getDayTime() < nexttrigger) return;

        // ======================================================== //

        nexttrigger += cooldown;

        clevel.setDayTime(clevel.getDayTime() - cooldown);

        SubtleMod.LOGGER.info(">>>> [Time Of Day] {}", clevel.getDayTime());

        setDirty();
    }
}
