package org.xuvigan.atlantacases;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CaseItem implements Listener {

    private final int id;
    private final int meta;
    private final int rarity;
    private final int minStackSize;
    private final int maxStackSize;
    private final int caseId;


    public CaseItem(int id, int meta, int rarity, int minStackSize, int maxStackSize, int caseId) {
        this.id = id;
        this.meta = meta;
        this.rarity = rarity;
        this.minStackSize = minStackSize;
        this.maxStackSize = maxStackSize;
        this.caseId = caseId;
    }

    public int getId() {
        return this.id;
    }

    public int getMeta() {
        return this.meta;
    }

    public int getRarity() {
        return this.rarity;
    }

    public int getMinStackSize() {
        return this.minStackSize;
    }

    public int getMaxStackSize() {
        return this.maxStackSize;
    }
@EventHandler
    public int getCaseId() {
        return this.caseId;
    }

}