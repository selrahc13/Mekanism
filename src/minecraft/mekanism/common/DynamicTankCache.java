package mekanism.common;

import java.util.HashSet;

import mekanism.api.Object3D;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

public class DynamicTankCache 
{
	public ItemStack[] inventory = new ItemStack[2];
	public LiquidStack liquid;
	
	public HashSet<Object3D> locations = new HashSet<Object3D>();
}
