package vazkii.minetunes.key;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import vazkii.minetunes.MineTunes;
import vazkii.minetunes.player.HUDHandler;

public class KeySubscriber {

    public static final KeySubscriber instance = new KeySubscriber();

    public static float delta = 0;
    float lastPartTicks = 0;
    boolean restarted = false;

    @SubscribeEvent
    public void playerTick(RenderTickEvent event) {
        delta = event.renderTickTime - lastPartTicks;
        if (delta < 0) delta = event.renderTickTime;
        lastPartTicks = event.renderTickTime;

        if (Minecraft.getMinecraft().thePlayer != null) {
            restarted = false;
            if (event.phase == Phase.START) {
                for (KeyBinding key : KeyBindings.handlers.keySet()) {
                    KeyHandler handler = KeyBindings.handlers.get(key);
                    if (key.getIsKeyPressed()) handler.keyDown(key);
                    else handler.keyUp(key);
                }
            } else HUDHandler.showVolume = false;
        } else if (MineTunes.musicPlayerThread != null) {
            if (!restarted) {
                MineTunes.musicPlayerThread.forceKill();
                MineTunes.startMusicPlayerThread();
                restarted = true;
            }
        }
    }

}
