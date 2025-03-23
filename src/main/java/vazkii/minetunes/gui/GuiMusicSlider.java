package vazkii.minetunes.gui;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;

public class GuiMusicSlider extends GuiButtonExt {
    public double sliderValue = 1.0F;

    public String dispString = "";
    public boolean dragging = false;
    public boolean showDecimal = true;
    public double minValue = 0.0D;
    public double maxValue = 5.0D;
    public int precision = 1;

    public ISlider parent = null;

    public String suffix = "";

    public boolean drawString = true;

    public GuiMusicSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal,
        double maxVal, double currentVal, boolean showDec, boolean drawStr) {
        this(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, null);
    }

    public GuiMusicSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal,
        double maxVal, double currentVal, boolean showDec, boolean drawStr, ISlider par) {
        super(id, xPos, yPos, width, height, prefix);
        minValue = minVal;
        maxValue = maxVal;
        sliderValue = (currentVal - minValue) / (maxValue - minValue);
        dispString = prefix;
        parent = par;
        suffix = suf;
        showDecimal = showDec;
        String val;

        if (showDecimal) {
            val = Double.toString(sliderValue * (maxValue - minValue) + minValue);
            precision = Math.min(
                val.substring(val.indexOf(".") + 1)
                    .length(),
                4);
        } else {
            val = Integer.toString((int) Math.round(sliderValue * (maxValue - minValue) + minValue));
            precision = 0;
        }

        displayString = dispString + val + suffix;

        drawString = drawStr;
        if (!drawString) {
            displayString = "";
        }
    }
    @Override
    public int getHoverState(boolean par1) {
        return 0;
    }
	
    @Override
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (par2 - (this.xPosition + 4)) / (float) (this.width - 8);
                updateSlider();
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(
                this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)),
                this.yPosition,
                0,
                66,
                4,
                20);
            this.drawTexturedModalRect(
                this.xPosition + (int) (this.sliderValue * (float) (this.width - 8)) + 4,
                this.yPosition,
                196,
                66,
                4,
                20);
        }
    }
	
    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
        if (super.mousePressed(par1Minecraft, par2, par3)) {
            this.sliderValue = (float) (par2 - (this.xPosition + 4)) / (float) (this.width - 8);
            updateSlider();
            return true;
        } else {
            return false;
        }
    }

    public void updateSlider() {
        if (this.sliderValue < 0.0F) {
            this.sliderValue = 0.0F;
        }

        if (this.sliderValue > 1.0F) {
            this.sliderValue = 1.0F;
        }

        String val;

        if (showDecimal) {
            val = Double.toString(sliderValue * (maxValue - minValue) + minValue);

            if (val.substring(val.indexOf(".") + 1)
                .length() > precision) {
                val = val.substring(0, val.indexOf(".") + precision + 1);

                if (val.endsWith(".")) {
                    val = val.substring(0, val.indexOf(".") + precision);
                }
            } else {
                while (val.substring(val.indexOf(".") + 1)
                    .length() < precision) {
                    val = val + "0";
                }
            }
        } else {
            val = Integer.toString((int) Math.round(sliderValue * (maxValue - minValue) + minValue));
        }

        if (drawString) {
            displayString = dispString + val + suffix;
        }

        if (parent != null) {
            parent.onChangeSliderValue(this);
        }
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        this.dragging = false;
    }
	
    public double getValue() {
        return sliderValue * (maxValue - minValue) + minValue;
    }

    public void setValue(double d) {
        this.sliderValue = (d - minValue) / (maxValue - minValue);
    }

    public static interface ISlider {
        void onChangeSliderValue(GuiMusicSlider slider);
    }
}
