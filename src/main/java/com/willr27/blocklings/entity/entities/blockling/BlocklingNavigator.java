package com.willr27.blocklings.entity.entities.blockling;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlocklingNavigator extends PathNavigator
{
    private boolean avoidSun;

    public BlocklingNavigator(MobEntity p_i45875_1_, World p_i45875_2_) {
        super(p_i45875_1_, p_i45875_2_);
    }

    protected PathFinder createPathFinder(int p_179679_1_) {
        this.nodeEvaluator = new WalkNodeProcessor();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, p_179679_1_);
    }

    protected boolean canUpdatePath() {
        return this.mob.isOnGround() || this.isInLiquid() || this.mob.isPassenger();
    }

    protected Vector3d getTempMobPos() {
        return new Vector3d(this.mob.getX(), (double)this.getSurfaceY(), this.mob.getZ());
    }

    public Path createPath(BlockPos p_179680_1_, int p_179680_2_) {
        if (this.level.getBlockState(p_179680_1_).isAir()) {
            BlockPos blockpos;
            for(blockpos = p_179680_1_.below(); blockpos.getY() > 0 && this.level.getBlockState(blockpos).isAir(); blockpos = blockpos.below()) {
            }

            if (blockpos.getY() > 0) {
                return super.createPath(blockpos.above(), p_179680_2_);
            }

            while(blockpos.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(blockpos).isAir()) {
                blockpos = blockpos.above();
            }

            p_179680_1_ = blockpos;
        }

        if (!this.level.getBlockState(p_179680_1_).getMaterial().isSolid()) {
            return super.createPath(p_179680_1_, p_179680_2_);
        } else {
            BlockPos blockpos1;
            for(blockpos1 = p_179680_1_.above(); blockpos1.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(blockpos1).getMaterial().isSolid(); blockpos1 = blockpos1.above()) {
            }

            return super.createPath(blockpos1, p_179680_2_);
        }
    }

    public Path createPath(Entity p_75494_1_, int p_75494_2_) {
        return this.createPath(p_75494_1_.blockPosition(), p_75494_2_);
    }

    private int getSurfaceY() {
        if (this.mob.isInWater() && this.canFloat()) {
            int i = MathHelper.floor(this.mob.getY());
            Block block = this.level.getBlockState(new BlockPos(this.mob.getX(), (double)i, this.mob.getZ())).getBlock();
            int j = 0;

            while(block == Blocks.WATER) {
                ++i;
                block = this.level.getBlockState(new BlockPos(this.mob.getX(), (double)i, this.mob.getZ())).getBlock();
                ++j;
                if (j > 16) {
                    return MathHelper.floor(this.mob.getY());
                }
            }

            return i;
        } else {
            return MathHelper.floor(this.mob.getY() + 0.5D);
        }
    }

    protected void trimPath() {
        super.trimPath();
        if (this.avoidSun) {
            if (this.level.canSeeSky(new BlockPos(this.mob.getX(), this.mob.getY() + 0.5D, this.mob.getZ()))) {
                return;
            }

            for(int i = 0; i < this.path.getNodeCount(); ++i) {
                PathPoint pathpoint = this.path.getNode(i);
                if (this.level.canSeeSky(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z))) {
                    this.path.truncateNodes(i);
                    return;
                }
            }
        }

    }

    @Override
    public boolean moveTo(@Nullable Path p_75484_1_, double p_75484_2_)
    {
        return super.moveTo(p_75484_1_, p_75484_2_);
    }

    @Override
    public void stop()
    {
        super.stop();
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    protected boolean canMoveDirectly(Vector3d p_75493_1_, Vector3d p_75493_2_, int p_75493_3_, int p_75493_4_, int p_75493_5_) {
        int i = MathHelper.floor(p_75493_1_.x);
        int j = MathHelper.floor(p_75493_1_.z);
        double d0 = p_75493_2_.x - p_75493_1_.x;
        double d1 = p_75493_2_.z - p_75493_1_.z;
        double d2 = d0 * d0 + d1 * d1;
        if (d2 < 1.0E-8D) {
            return false;
        } else {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            p_75493_3_ = p_75493_3_ + 2;
            p_75493_5_ = p_75493_5_ + 2;
            if (!this.canWalkOn(i, MathHelper.floor(p_75493_1_.y), j, p_75493_3_, p_75493_4_, p_75493_5_, p_75493_1_, d0, d1)) {
                return false;
            } else {
                p_75493_3_ = p_75493_3_ - 2;
                p_75493_5_ = p_75493_5_ - 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double)i - p_75493_1_.x;
                double d7 = (double)j - p_75493_1_.z;
                if (d0 >= 0.0D) {
                    ++d6;
                }

                if (d1 >= 0.0D) {
                    ++d7;
                }

                d6 = d6 / d0;
                d7 = d7 / d1;
                int k = d0 < 0.0D ? -1 : 1;
                int l = d1 < 0.0D ? -1 : 1;
                int i1 = MathHelper.floor(p_75493_2_.x);
                int j1 = MathHelper.floor(p_75493_2_.z);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while(k1 * k > 0 || l1 * l > 0) {
                    if (d6 < d7) {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    } else {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }

                    if (!this.canWalkOn(i, MathHelper.floor(p_75493_1_.y), j, p_75493_3_, p_75493_4_, p_75493_5_, p_75493_1_, d0, d1)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private boolean canWalkOn(int p_179683_1_, int p_179683_2_, int p_179683_3_, int p_179683_4_, int p_179683_5_, int p_179683_6_, Vector3d p_179683_7_, double p_179683_8_, double p_179683_10_) {
        int i = p_179683_1_ - p_179683_4_ / 2;
        int j = p_179683_3_ - p_179683_6_ / 2;
        if (!this.canWalkAbove(i, p_179683_2_, j, p_179683_4_, p_179683_5_, p_179683_6_, p_179683_7_, p_179683_8_, p_179683_10_)) {
            return false;
        } else {
            for(int k = i; k < i + p_179683_4_; ++k) {
                for(int l = j; l < j + p_179683_6_; ++l) {
                    double d0 = (double)k + 0.5D - p_179683_7_.x;
                    double d1 = (double)l + 0.5D - p_179683_7_.z;
                    if (!(d0 * p_179683_8_ + d1 * p_179683_10_ < 0.0D)) {
                        PathNodeType pathnodetype = this.nodeEvaluator.getBlockPathType(this.level, k, p_179683_2_ - 1, l, this.mob, p_179683_4_, p_179683_5_, p_179683_6_, true, true);
                        if (!this.hasValidPathType(pathnodetype)) {
                            return false;
                        }

                        pathnodetype = this.nodeEvaluator.getBlockPathType(this.level, k, p_179683_2_, l, this.mob, p_179683_4_, p_179683_5_, p_179683_6_, true, true);
                        float f = this.mob.getPathfindingMalus(pathnodetype);
                        if (f < 0.0F || f >= 8.0F) {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    protected boolean hasValidPathType(PathNodeType p_230287_1_) {
        if (p_230287_1_ == PathNodeType.WATER) {
            return false;
        } else if (p_230287_1_ == PathNodeType.LAVA) {
            return false;
        } else {
            return p_230287_1_ != PathNodeType.OPEN;
        }
    }

    private boolean canWalkAbove(int p_179692_1_, int p_179692_2_, int p_179692_3_, int p_179692_4_, int p_179692_5_, int p_179692_6_, Vector3d p_179692_7_, double p_179692_8_, double p_179692_10_) {
        for(BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(p_179692_1_, p_179692_2_, p_179692_3_), new BlockPos(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1))) {
            double d0 = (double)blockpos.getX() + 0.5D - p_179692_7_.x;
            double d1 = (double)blockpos.getZ() + 0.5D - p_179692_7_.z;
            if (!(d0 * p_179692_8_ + d1 * p_179692_10_ < 0.0D) && !this.level.getBlockState(blockpos).isPathfindable(this.level, blockpos, PathType.LAND)) {
                return false;
            }
        }

        return true;
    }

    public void setCanOpenDoors(boolean p_179688_1_) {
        this.nodeEvaluator.setCanOpenDoors(p_179688_1_);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setAvoidSun(boolean p_179685_1_) {
        this.avoidSun = p_179685_1_;
    }
}