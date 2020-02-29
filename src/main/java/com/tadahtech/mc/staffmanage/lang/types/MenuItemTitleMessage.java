package com.tadahtech.mc.staffmanage.lang.types;

import com.tadahtech.mc.staffmanage.lang.MessageKey;
import com.tadahtech.mc.staffmanage.util.Colors;

/**
 *
 */
public class MenuItemTitleMessage extends PrefixedMessage {

    public MenuItemTitleMessage(MessageKey title) {
        super(title, Colors.MENU_ITEM);
    }
}
