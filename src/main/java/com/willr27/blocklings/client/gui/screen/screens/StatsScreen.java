package com.willr27.blocklings.client.gui.screen.screens;

import com.willr27.blocklings.client.gui.GuiTextures;
import com.willr27.blocklings.client.gui.control.*;
import com.willr27.blocklings.client.gui.control.controls.*;
import com.willr27.blocklings.client.gui.control.controls.stats.HealthBarControl;
import com.willr27.blocklings.client.gui.control.controls.stats.StatControl;
import com.willr27.blocklings.client.gui2.Colour;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.item.BlocklingsItems;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Random;

/**
 * A screen to display the blockling's stats.
 */
@OnlyIn(Dist.CLIENT)
public class StatsScreen extends TabbedScreen
{
    /**
     * @param blockling the blockling.
     */
    public StatsScreen(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, TabbedControl.Tab.STATS);
    }

    @Override
    protected void init()
    {
        super.init();

        TextFieldControl textFieldControl = new TextFieldControl();
        textFieldControl.setParent(contentControl);
        textFieldControl.setDock(Dock.TOP);
        textFieldControl.setHorizontalAlignment(HorizontalAlignment.MIDDLE);
        textFieldControl.setMaxTextLength(25);
        textFieldControl.setText(blockling.getCustomName().getString());
        textFieldControl.focusChanged.subscribe((e) ->
        {
            if (!textFieldControl.getText().trim().isEmpty())
            {
                blockling.setCustomName(new StringTextComponent(textFieldControl.getText()), true);
            }
            else
            {
                ITextComponent name = BlocklingsItems.BLOCKLING.get().getName(BlocklingsItems.BLOCKLING.get().getDefaultInstance());
                blockling.setCustomName(new StringTextComponent(name.getString()), true);
                textFieldControl.setText(name.getString());
            }
        });

        Control statsControl = new Control();
        statsControl.setParent(contentControl);
        statsControl.setDock(Dock.FILL);

        Control healthControl = new Control();
        healthControl.setParent(statsControl);
        healthControl.setPercentHeight(0.14f);
        healthControl.setDock(Dock.TOP);

        HealthBarControl healthBarControl = new HealthBarControl(blockling);
        healthBarControl.setParent(healthControl);
        healthBarControl.setPercentX(0.5f);
        healthBarControl.setPercentY(0.5f);

        Control levelsControl = new Control();
        levelsControl.setParent(statsControl);
        levelsControl.setPercentHeight(0.36f);
        levelsControl.setDock(Dock.BOTTOM);
        levelsControl.setBackgroundColour(new Colour(0xff99ff11));

        Control leftStatsControl = new Control();
        leftStatsControl.setParent(statsControl);
        leftStatsControl.setPercentWidth(0.5f);
        leftStatsControl.setPadding(Side.LEFT, 10);
        leftStatsControl.setDock(Dock.LEFT);
        leftStatsControl.setBackgroundColour(new Colour(0xffC3FF1E));

        Control rightStatsControl = new Control();
        rightStatsControl.setParent(statsControl);
        rightStatsControl.setPercentWidth(0.5f);
        rightStatsControl.setPadding(Side.RIGHT, 10);
        rightStatsControl.setDock(Dock.RIGHT);
        rightStatsControl.setBackgroundColour(new Colour(0xffFFCA99));

        Random random = new Random();

        EnumeratingControl combatStats = new EnumeratingControl();
        combatStats.setParent(leftStatsControl);
        combatStats.setPercentX(0.0f);
        combatStats.setBackgroundColour(new Colour(0xffBFB2FF));
        combatStats.setAlignmentY(new Alignment(0.25f));

        StatControl mainDamageStat = new StatControl(GuiTextures.Stats.ATTACK_DAMAGE_MAIN, () -> random.nextFloat() > 0.5f ? "100" : "1", () -> new ArrayList<>(), true);
        mainDamageStat.setParent(combatStats);
        mainDamageStat.setBackgroundColour(new Colour(0xff2FFFEE));

        EnumeratingControl defenceStats = new EnumeratingControl();
        defenceStats.setParent(leftStatsControl);
        defenceStats.setPercentX(0.0f);
        defenceStats.setBackgroundColour(new Colour(0xffBFB2FF));
        defenceStats.setAlignmentY(new Alignment(0.75f));

        StatControl armourStat = new StatControl(GuiTextures.Stats.ATTACK_DAMAGE_MAIN, () -> random.nextFloat() > 0.5f ? "100" : "1", () -> new ArrayList<>(), true);
        armourStat.setParent(defenceStats);
        armourStat.setBackgroundColour(new Colour(0xff2FFFEE));

        EnumeratingControl gatherStats = new EnumeratingControl();
        gatherStats.setParent(rightStatsControl);
        gatherStats.setPercentX(1.0f);
        gatherStats.setBackgroundColour(new Colour(0xffBFB2FF));
        gatherStats.setAlignmentX(new Alignment(1.0f));
        gatherStats.setAlignmentY(new Alignment(0.25f));

        StatControl miningSpeedStat = new StatControl(GuiTextures.Stats.ATTACK_DAMAGE_MAIN, () -> random.nextFloat() > 0.5f ? "100" : "1", () -> new ArrayList<>(), true);
        miningSpeedStat.setParent(gatherStats);
        miningSpeedStat.setBackgroundColour(new Colour(0xff2FFFEE));

        EnumeratingControl movementStats = new EnumeratingControl();
        movementStats.setParent(rightStatsControl);
        movementStats.setPercentX(1.0f);
        movementStats.setBackgroundColour(new Colour(0xffBFB2FF));
        movementStats.setAlignmentX(new Alignment(1.0f));
        movementStats.setAlignmentY(new Alignment(0.75f));

        StatControl moveSpeedStat = new StatControl(GuiTextures.Stats.ATTACK_DAMAGE_MAIN, () -> random.nextFloat() > 0.5f ? "100" : "1", () -> new ArrayList<>(), true);
        moveSpeedStat.setParent(movementStats);
        moveSpeedStat.setBackgroundColour(new Colour(0xff2FFFEE));

        EntityControl entityControl = new EntityControl();
        entityControl.setParent(statsControl);
        entityControl.setEntity(blockling);
        entityControl.setWidth(48);
        entityControl.setHeight(48);
        entityControl.setScaleToBoundingBox(true);
        entityControl.setEntityScale(1.0f);
        entityControl.setOffsetY(0.0f);
        entityControl.setShouldScissor(false);
        entityControl.setPercentX(0.5f);
        entityControl.setPercentY(0.3f);
        entityControl.setBackgroundColour(new Colour(0xffffffff));
    }
}
