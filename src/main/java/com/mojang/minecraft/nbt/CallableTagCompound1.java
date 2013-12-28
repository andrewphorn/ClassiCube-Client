package com.mojang.minecraft.nbt;

import java.util.concurrent.Callable;

class CallableTagCompound1 implements Callable
{
    final String field_82585_a;

    final NBTTagCompound theNBTTagCompound;

    CallableTagCompound1(NBTTagCompound par1NBTTagCompound, String par2Str)
    {
        this.theNBTTagCompound = par1NBTTagCompound;
        this.field_82585_a = par2Str;
    }

    public String func_82583_a()
    {
        return NBTBase.NBTTypes[((NBTBase)NBTTagCompound.getTagMap(this.theNBTTagCompound).get(this.field_82585_a)).getId()];
    }

    public Object call()
    {
        return this.func_82583_a();
    }
}
