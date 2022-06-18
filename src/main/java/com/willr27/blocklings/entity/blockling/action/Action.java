package com.willr27.blocklings.entity.blockling.action;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.network.BlocklingMessage;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.util.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class Action
{
    /**
     * The blockling.
     */
    public final BlocklingEntity blockling;

    /**
     * The key used to identify the action.
     */
    public final String key;

    /**
     * The side that has authority over the value of the action.
     */
    public final Authority authority;

    /**
     * The current count of the action.
     */
    protected float count;

    /**
     * The list of callbacks to call when an action finishes.
     */
    protected final List<Runnable> callbacks = new ArrayList<>();

    /**
     * @param blockling the blockling.
     * @param key the key used to identify the action.
     * @param authority the side that has authority over the value of the action.
     */
    public Action(@Nonnull BlocklingEntity blockling, @Nonnull String key, @Nonnull Authority authority)
    {
        this.blockling = blockling;
        this.key = key;
        this.authority = authority;
    }

    /**
     * Adds the given callback to the list of callbacks to call when an action finishes.
     */
    public void addCallback(@Nonnull Runnable callback)
    {
        callbacks.add(callback);
    }

    /**
     * Calls all the callbacks that should be called when an action finishes.
     */
    public void callCallbacks()
    {
        for (Runnable callback : callbacks)
        {
            callback.run();
        }
    }

    /**
     * Starts the action if it is not already being performed.
     *
     * @return true if the action was started.
     */
    public boolean tryStart()
    {
        if (isRunning())
        {
            return false;
        }
        else
        {
            start();

            return true;
        }
    }

    /**
     * Starts the action whether it's running or not.
     */
    public void start()
    {
        setCount(0.0f);
    }

    /**
     * Increments the count by 1.0f.
     */
    public void tick()
    {
        tick(1.0f);
    }

    /**
     * Increments the count by the given amount.
     */
    public void tick(float increment)
    {
        if (isRunning())
        {
            setCount(count + increment);
        }
    }

    /**
     * Stops the action if it is running.
     */
    public void stop()
    {
        if (isRunning())
        {
            setCount(-1.0f);
        }
    }

    /**
     * @return true if the action is currently running.
     */
    public boolean isRunning()
    {
        return count != -1;
    }

    /**
     * @return the current value of the count attribute.
     */
    public float getCount()
    {
        return count;
    }

    /**
     * Sets the count to the given value.
     *
     * @param count the value to set the count to.
     */
    public void setCount(float count)
    {
        setCount(count, isCorrectSide() && authority != Authority.NONE);
    }

    /**
     * Sets the count to the given value
     *
     * @param count the value to set the count to.
     * @param sync whether to sync to the client/server.
     */
    public void setCount(float count, boolean sync)
    {
        this.count = count;

        if (sync)
        {
            NetworkHandler.sync(blockling.level, new CountMessage(blockling, key, count));
        }
    }

    /**
     * @param targetCount the target counter value.
     * @return the percentage towards the target count.
     */
    public float percentThroughAction(float targetCount)
    {
        return getCount() / (float) targetCount;
    }

    /**
     * @return true if the authority matches the side.
     */
    public boolean isCorrectSide()
    {
        return authority == Authority.BOTH || authority == Authority.NONE || (authority == Authority.SERVER && !blockling.level.isClientSide) || (authority == Authority.CLIENT && blockling.level.isClientSide);
    }

    /**
     * Which side has authority over the action.
     */
    public static enum Authority
    {
        BOTH,
        CLIENT,
        SERVER,
        NONE
    }

    /**
     * Used to sync the value of count between the client and server.
     */
    public static class CountMessage extends BlocklingMessage<CountMessage>
    {
        /**
         * The key of the action.
         */
        private String key;

        /**
         * The count of the action.
         */
        private float count;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public CountMessage()
        {
            super(null);
        }

        /**
         * @param blockling the blockling.
         * @param key the key of the action.
         * @param count the count of the action.
         */
        public CountMessage(@Nullable BlocklingEntity blockling, @Nonnull String key, float count)
        {
            super(blockling);
            this.key = key;
            this.count = count;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            PacketBufferUtils.writeString(buf, key);
            buf.writeFloat(count);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            key = PacketBufferUtils.readString(buf);
            count = buf.readFloat();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling)
        {
            Action action = blockling.getActions().find(key);

            if (action != null)
            {
                action.setCount(count, false);
            }
        }
    }
}
