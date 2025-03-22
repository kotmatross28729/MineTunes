package vazkii.minetunes.key;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import vazkii.minetunes.gui.GuiPlaylistManager;

public abstract class KeyHandler {

    public abstract void keyDown(KeyBinding key);

    public abstract void keyUp(KeyBinding key);

    public boolean isInValidGui() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.currentScreen == null || mc.currentScreen instanceof GuiPlaylistManager;
    }

}
