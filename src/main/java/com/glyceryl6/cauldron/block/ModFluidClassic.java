package com.glyceryl6.cauldron.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

@SuppressWarnings("deprecation")
public class ModFluidClassic extends BlockFluidClassic {

    public ModFluidClassic(Fluid fluid, Material material) {
        super(fluid, material);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

}