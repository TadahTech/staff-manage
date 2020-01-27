package com.tadahtech.mc.staffmanage.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.util.EnumMap;

/**
 * Variables both Bungee and Spigot modules have in common
 */
public class Common {

    public static final String DISCORD_BOT_CHANNEL = "discord.bot";
    public static final String SMP_PLUGINCHANNEL = "smp.plugin";

    public static final Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .registerTypeAdapter(EnumMap.class, new EnumMapAdapter())
      .create();

    public static final JsonParser PARSER = new JsonParser();
}
