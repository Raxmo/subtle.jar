package io.liska.subtle;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs

// TODO: populate with actually useful stuffs

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // ------------ TEMPLATE ------------ //
    public static final ModConfigSpec.IntValue TEMPLATE_NUMBER;
    public static final ModConfigSpec.BooleanValue TEMPLATE_BOOL;
    public static final ModConfigSpec.DoubleValue TEMPLATE_DOUBLE;
    static
    {
        BUILDER.push("Template");

        TEMPLATE_NUMBER = BUILDER
                .comment("A template int")
                .defineInRange("Template Number", 42, 0, 255);

        TEMPLATE_BOOL = BUILDER
                .comment("A template boolean")
                .define("TemplateBool", false);

        TEMPLATE_DOUBLE = BUILDER
                .comment("A template double")
                .defineInRange("Template Double", 0.5, 0.0, 1.0);

    }

    static{ SPEC = BUILDER.build(); }
    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
