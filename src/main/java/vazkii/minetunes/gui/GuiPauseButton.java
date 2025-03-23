package vazkii.minetunes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPauseButton extends GuiButton {

    public GuiPauseButton(int stateName, int id, int p_i1021_3_, int p_i1021_4_, int p_i1021_5_, String p_i1021_6_) {
        super(stateName, id, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
    }

    public boolean paused = true;

    protected static final ResourceLocation pauseTexture = new ResourceLocation(
        "minetunes",
        "textures/gui/pauseButton0.png");
    protected static final ResourceLocation notPauseTexture = new ResourceLocation(
        "minetunes",
        "textures/gui/pauseButton1.png");

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;

            mc.getTextureManager()
                .bindTexture(paused ? pauseTexture : notPauseTexture);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(
                this.xPosition + this.width / 2,
                this.yPosition,
                200 - this.width / 2,
                46 + k * 20,
                this.width / 2,
                this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int l = 14737632;

            if (packedFGColour != 0) {
                l = packedFGColour;
            } else if (!this.enabled) {
                l = 10526880;
            } else if (this.field_146123_n) {
                l = 16777120;
            }

            this.drawCenteredString(
                fontrenderer,
                this.displayString,
                this.xPosition + this.width / 2,
                this.yPosition + (this.height - 8) / 2,
                l);
        }
    }
}
