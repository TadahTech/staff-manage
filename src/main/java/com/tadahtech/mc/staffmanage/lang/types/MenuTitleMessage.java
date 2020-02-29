package com.tadahtech.mc.staffmanage.lang.types;

import com.tadahtech.mc.staffmanage.lang.MessageKey;
import com.tadahtech.mc.staffmanage.util.Colors;

/**
 *
 */
public class MenuTitleMessage extends PrefixedMessage {

    public MenuTitleMessage(MessageKey menuTitle) {
        super(menuTitle, Colors.BLACK + Colors.BOLD);
    }

}
