package mekanism.additions.common.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.additions.common.entity.EntityObsidianTNT;
import mekanism.common.util.MultipartUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockObsidianTNT extends TNTBlock {

    private static final VoxelShape bounds = MultipartUtils.combine(
          makeCuboidShape(0, 0, 0, 16, 3, 16),//Wooden1
          makeCuboidShape(0, 8, 0, 16, 11, 16),//Wooden2
          makeCuboidShape(12.5, 11.8, 12.5, 13.5, 13.8, 13.5),//Wick1
          makeCuboidShape(12.5, 11.5, 7.5, 13.5, 13.5, 8.5),//Wick2
          makeCuboidShape(12.5, 11.8, 2.5, 13.5, 13.8, 3.5),//Wick3
          makeCuboidShape(2.5, 11.8, 12.5, 3.5, 13.8, 13.5),//Wick4
          makeCuboidShape(2.5, 11.5, 7.5, 3.5, 13.5, 8.5),//Wick5
          makeCuboidShape(2.5, 11.8, 2.5, 3.5, 13.8, 3.5),//Wick6
          makeCuboidShape(7.5, 11.5, 12.5, 8.5, 13.5, 13.5),//Wick7
          makeCuboidShape(7.5, 11.5, 2.5, 8.5, 13.5, 3.5),//Wick8
          makeCuboidShape(7.5, 11.8, 7.5, 8.5, 13.8, 8.5),//Wick9
          makeCuboidShape(11, -1, 11, 15, 12, 15),//Rod1
          makeCuboidShape(11, -1, 6, 15, 12, 10),//Rod2
          makeCuboidShape(11, -1, 1, 15, 12, 5),//Rod3
          makeCuboidShape(6, -1, 1, 10, 12, 5),//Rod4
          makeCuboidShape(6, -1, 6, 10, 12, 10),//Rod5
          makeCuboidShape(6, -1, 11, 10, 12, 15),//Rod6
          makeCuboidShape(1, -1, 6, 5, 12, 10),//Rod7
          makeCuboidShape(1, -1, 11, 5, 12, 15),//Rod8
          makeCuboidShape(1, -1, 1, 5, 12, 5)//Rod9
    );

    public BlockObsidianTNT() {
        super(Block.Properties.create(Material.TNT));
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        //300 is 100% chance fire will spread to this block, 100 is default for TNT
        // Given we are "obsidian" make ours slightly more stable against fire being spread than vanilla TNT
        return 75;
    }

    @Override
    public void catchFire(BlockState state, World world, @Nonnull BlockPos pos, @Nullable Direction side, @Nullable LivingEntity igniter) {
        if (!world.isRemote) {
            TNTEntity tnt = new EntityObsidianTNT(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, igniter);
            world.addEntity(tnt);
            world.playSound(null, tnt.posX, tnt.posY, tnt.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void onExplosionDestroy(World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
        if (!world.isRemote) {
            TNTEntity tnt = new EntityObsidianTNT(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, explosion.getExplosivePlacedBy());
            tnt.setFuse((short) (world.rand.nextInt(tnt.getFuse() / 4) + tnt.getFuse() / 8));
            world.addEntity(tnt);
        }
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return bounds;
    }
}