package mekanism.common.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.NBTConstants;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

/**
 * Multi-block used by wind turbines, solar panels, and other machines
 */
public class TileEntityBoundingBlock extends TileEntityUpdateable {

    //TODO: Do we just want to promote the bounding block to being "advanced" in terms of how things are proxied to the main block, rather than
    // have the extra stuff only happen with the advanced variant. Or do we want to at least move support for the offset capability stuff. to here
    private BlockPos mainPos = BlockPos.ZERO;

    public boolean receivedCoords;

    private int currentRedstoneLevel;

    public TileEntityBoundingBlock() {
        this(MekanismTileEntityTypes.BOUNDING_BLOCK.getTileEntityType());
    }

    public TileEntityBoundingBlock(TileEntityType<TileEntityBoundingBlock> type) {
        super(type);
    }

    public void setMainLocation(BlockPos pos) {
        receivedCoords = pos != null;
        if (!isRemote()) {
            mainPos = pos;
            sendUpdatePacket();
        }
    }

    public BlockPos getMainPos() {
        if (mainPos == null) {
            mainPos = BlockPos.ZERO;
        }
        return mainPos;
    }

    @Nullable
    public TileEntity getMainTile() {
        return receivedCoords ? MekanismUtils.getTileEntity(world, getMainPos()) : null;
    }

    public void onNeighborChange(BlockState state) {
        final TileEntity tile = getMainTile();
        if (tile instanceof TileEntityMekanism) {
            int power = world.getRedstonePowerFromNeighbors(getPos());
            if (currentRedstoneLevel != power) {
                if (power > 0) {
                    onPower();
                } else {
                    onNoPower();
                }
                currentRedstoneLevel = power;
                ((TileEntityMekanism) tile).sendUpdatePacket(this);
            }
        }
    }

    public void onPower() {
    }

    public void onNoPower() {
    }

    @Override
    public void read(CompoundNBT nbtTags) {
        super.read(nbtTags);
        NBTUtils.setBlockPosIfPresent(nbtTags, NBTConstants.MAIN, pos -> mainPos = pos);
        currentRedstoneLevel = nbtTags.getInt(NBTConstants.REDSTONE);
        receivedCoords = nbtTags.getBoolean(NBTConstants.RECEIVED_COORDS);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT nbtTags) {
        super.write(nbtTags);
        nbtTags.put(NBTConstants.MAIN, NBTUtil.writeBlockPos(getMainPos()));
        nbtTags.putInt(NBTConstants.REDSTONE, currentRedstoneLevel);
        nbtTags.putBoolean(NBTConstants.RECEIVED_COORDS, receivedCoords);
        return nbtTags;
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT updateTag = super.getUpdateTag();
        updateTag.put(NBTConstants.MAIN, NBTUtil.writeBlockPos(getMainPos()));
        updateTag.putInt(NBTConstants.REDSTONE, currentRedstoneLevel);
        updateTag.putBoolean(NBTConstants.RECEIVED_COORDS, receivedCoords);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundNBT tag) {
        super.handleUpdateTag(tag);
        NBTUtils.setBlockPosIfPresent(tag, NBTConstants.MAIN, pos -> mainPos = pos);
        currentRedstoneLevel = tag.getInt(NBTConstants.REDSTONE);
        receivedCoords = tag.getBoolean(NBTConstants.RECEIVED_COORDS);
    }
}