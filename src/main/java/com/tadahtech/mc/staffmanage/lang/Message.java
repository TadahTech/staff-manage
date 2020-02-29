package com.tadahtech.mc.staffmanage.lang;

import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.Locale;

public interface Message extends Serializable {

    String toString(Locale locale);

    String toString(Player player);

}
