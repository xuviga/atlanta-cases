//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\XuViGaN\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package com.mayakplay.doge;

public class CaseItem
{
    private int id;
    private int meta;
    private int rarity;
    private int minStackSize;
    private int maxStackSize;
    private int caseId;
    
    public CaseItem(final int id, final int meta, final int rarity, final int minStackSize, final int maxStackSize, final int caseId) {
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
