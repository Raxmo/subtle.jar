package io.liska.subtle;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs

// TODO: populate with actually useful stuffs

// NOTE: in the constructor of parts, use "type value = Config.VARIABLE.get();" to assign value

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ======================== Template ======================== //
    static {BUILDER.push("Template");}
    public static final ModConfigSpec.BooleanValue TEMPLATE_BOOL = BUILDER
            .comment("A template bollean")
            .define("TempBool", false);

    public static final ModConfigSpec.IntValue TEMPLATE_INT = BUILDER
            .comment("A Template Int")
            .defineInRange("TempInt", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> TEMPLATE_LIST_ITEMS = BUILDER
            .comment("A Template List of Item names")
            .defineListAllowEmpty("Items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);
    static{BUILDER.pop();}
    // ============================================================ //

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
