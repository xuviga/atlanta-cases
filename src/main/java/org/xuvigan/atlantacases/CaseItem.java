package org.xuvigan.atlantacases;
public class CaseItem {
    private int id;
    private int meta;
    private int rarity;
    private int minStackSize;
    private int maxStackSize;
    private int caseId;

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

    public int getCaseId() {
        return this.caseId;
    }
}