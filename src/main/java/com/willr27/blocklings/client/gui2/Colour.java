package com.willr27.blocklings.client.gui2;

import com.mojang.blaze3d.systems.RenderSystem;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * A class to represent a colour.
 */
public class Colour
{
    /**
     * The red component of the colour.
     */
    private float r;

    /**
     * The green component of the colour.
     */
    private float g;

    /**
     * The blue component of the colour.
     */
    private float b;

    /**
     * The alpha component of the colour.
     */
    private float a;

    /**
     * @param colour the colour.
     */
    public Colour(@Nonnull Color colour)
    {
        setColour(colour);
    }

    /**
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     */
    public Colour(float r, float g, float b)
    {
        this(r, g, b, 1.0f);
    }

    /**
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     * @param a the alpha value.
     */
    public Colour(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Applies the colour to the render system.
     */
    public void apply()
    {
        RenderSystem.color4f(r, g, b, a);
    }

    /**
     * Sets the colour.
     *
     * @param colour the colour.
     */
    public void setColour(@Nonnull Color colour)
    {
        r = colour.getRed() / 255.0f;
        g = colour.getGreen() / 255.0f;
        b = colour.getBlue() / 255.0f;
        a = colour.getAlpha() / 255.0f;
    }

    /**
     * Gets the red value.
     *
     * @return the red value.
     */
    public float getR()
    {
        return r;
    }

    /**
     * Gets the green value.
     *
     * @return the green value.
     */
    public float getG()
    {
        return g;
    }

    /**
     * Gets the blue value.
     *
     * @return the blue value.
     */
    public float getB()
    {
        return b;
    }

    /**
     * Gets the alpha value.
     *
     * @return the alpha value.
     */
    public float getA()
    {
        return a;
    }

    /**
     * Sets the red value.
     *
     * @param r the red value.
     */
    public void setR(float r)
    {
        this.r = r;
    }

    /**
     * Sets the green value.
     *
     * @param g the green value.
     */
    public void setG(float g)
    {
        this.g = g;
    }

    /**
     * Sets the blue value.
     *
     * @param b the blue value.
     */
    public void setB(float b)
    {
        this.b = b;
    }

    /**
     * Sets the alpha value.
     *
     * @param a the alpha value.
     */
    public void setA(float a)
    {
        this.a = a;
    }
}
