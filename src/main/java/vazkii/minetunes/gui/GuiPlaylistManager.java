package vazkii.minetunes.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import vazkii.minetunes.MineTunes;
import vazkii.minetunes.config.MTConfig;
import vazkii.minetunes.player.chooser.FileSelector;
import vazkii.minetunes.player.chooser.action.ActionMakePlaylist;
import vazkii.minetunes.player.chooser.filter.PlaylistFilter;
import vazkii.minetunes.playlist.MP3Metadata;
import vazkii.minetunes.playlist.Playlist;
import vazkii.minetunes.playlist.PlaylistList;

public class GuiPlaylistManager extends GuiMineTunes {

    public volatile static int currentPlaylist = 0;
    public volatile static int currentSong = 0;

    static int selectedPlaylist = 0;
    static MP3Metadata selectedSong = null;

    GuiPlaylistSlot playlistSlot;
    GuiMusicSlot musicSlot;

    GuiButton devOptionsButton;
    GuiButton showHudButton;
    GuiButton moveHudButton;
    GuiButton playModeButton;

    GuiButton timeDisplayModeButton;

    GuiButton selectPlaylistButton;
    GuiButton deletePlaylistButton;
    GuiButton reloadPlaylistButton;

    GuiTextField playlistNameField;

    GuiTextField searchField;

    public List<MP3Metadata> visibleSongs = new ArrayList();

    GuiMusicSlider songPositionSlider;

    GuiPauseButton pauseButton;

    @Override
    public void initGui() {
        buttonList.clear();

        buttonList.add(
            devOptionsButton = new GuiButton(
                1,
                185,
                55,
                100,
                20,
                StatCollector.translateToLocal("minetunes.gui.devTools")));
        buttonList.add(
            showHudButton = new GuiButton(
                2,
                5,
                5,
                90,
                20,
                StatCollector.translateToLocal("minetunes.gui.showHud_true")));
        buttonList.add(
            moveHudButton = new GuiButton(3, 105, 5, 70, 20, StatCollector.translateToLocal("minetunes.gui.move")));

        buttonList.add(
            playModeButton = new GuiButton(
                4,
                5,
                30,
                170,
                20,
                StatCollector.translateToLocal("minetunes.gui.playMode0")));

        buttonList.add(
            timeDisplayModeButton = new GuiButton(
                8,
                185,
                5,
                170,
                20,
                StatCollector.translateToLocal("minetunes.gui.timeDisplayMode0")));

        buttonList.add(
            selectPlaylistButton = new GuiButton(
                5,
                360,
                55,
                125,
                20,
                StatCollector.translateToLocal("minetunes.gui.selectPlaylist")));

        buttonList.add(
            deletePlaylistButton = new GuiButton(
                6,
                10,
                height - 25,
                88,
                20,
                StatCollector.translateToLocal("minetunes.gui.delete")));
        buttonList.add(
            reloadPlaylistButton = new GuiButton(
                7,
                101,
                height - 25,
                88,
                20,
                StatCollector.translateToLocal("minetunes.gui.reload")));

        buttonList.add(pauseButton = new GuiPauseButton(10, 335, 55, 20, 20, ""));

        buttonList.add(songPositionSlider = new GuiMusicSlider(9, 185, 30, 170, 20, "", "", 0, 100, 0, false, false));

        playlistNameField = new GuiTextField(fontRendererObj, 360, 30, 125, 20);
        playlistNameField.setMaxStringLength(32);

        searchField = new GuiTextField(fontRendererObj, 5, 55, 170, 20);
        searchField.setFocused(true);

        playlistSlot = new GuiPlaylistSlot(this);
        musicSlot = new GuiMusicSlot(this);

        updateVisibleSongs();
    }

    @Override
    public void drawScreen(int mx, int my, float partialTicks) {
        if (MineTunes.musicPlayerThread != null) {
            pauseButton.paused = MineTunes.musicPlayerThread.isPaused();
        }

        musicSlot.drawScreen(mx, my, partialTicks);
        playlistSlot.drawScreen(mx, my, partialTicks);

        drawRect(0, 0, width, getTopSize(), 0xFF000000);
        drawRect(197, getTopSize(), 200, height, 0xFF000000);

        if (PlaylistList.playlistNames.isEmpty()) {
            String s = StatCollector.translateToLocal("minetunes.gui.noPlaylists");
            int sWidth = mc.fontRenderer.getStringWidth(s);
            int xp = 100 - sWidth / 2;
            int yp = height / 2 - 5;
            drawBox(xp - 10, yp - 10, sWidth + 20, 30);
            mc.fontRenderer.drawStringWithShadow(s, xp, yp, 0xFF4444);
        }

        GL11.glPushMatrix();
        GL11.glScalef(3F, 3F, 3F);
        boolean unicode = mc.fontRenderer.getUnicodeFlag();
        mc.fontRenderer.setUnicodeFlag(true);
        // mc.fontRenderer.drawString(StatCollector.translateToLocal("minetunes.gui.title"), 2, 1, 0xFFFFFF);
        mc.fontRenderer.setUnicodeFlag(unicode);
        GL11.glPopMatrix();

        if (MineTunes.musicPlayerThread != null && MineTunes.musicPlayerThread.getPlayingMetadata() != null) {
            songPositionSlider.sliderValue = MineTunes.musicPlayerThread.getFractionPlayed();
        }

        boolean hasName = !playlistNameField.getText()
            .isEmpty();
        boolean creatingPlaylist = MineTunes.playlistCreatorThread != null;
        if (creatingPlaylist) {
            String creating = EnumChatFormatting.DARK_AQUA
                + StatCollector.translateToLocal("minetunes.gui.creatingPlaylist");
            String status = MineTunes.playlistCreatorThread.getProgressState();

            fontRendererObj
                .drawStringWithShadow(creating, width - 10 - fontRendererObj.getStringWidth(creating), 30, 0xFFFFFF);
            fontRendererObj
                .drawStringWithShadow(status, width - 10 - fontRendererObj.getStringWidth(status), 42, 0xFFFFFF);
        } else {
            mc.fontRenderer
                .drawString(StatCollector.translateToLocal("minetunes.gui.playlistCreator"), 360, 20, 0xFFFFFF);

            if (!hasName) {
                String name = StatCollector.translateToLocal("minetunes.gui.playlistName");
                drawCenteredString(
                    fontRendererObj,
                    name,
                    playlistNameField.xPosition + playlistNameField.width / 2,
                    playlistNameField.yPosition + 6,
                    0x444444);
            }
            playlistNameField.drawTextBox();
        }

        devOptionsButton.visible = isCtrlKeyDown() && isShiftKeyDown();
        selectPlaylistButton.visible = !creatingPlaylist;
        selectPlaylistButton.enabled = hasName;
        showHudButton.displayString = StatCollector.translateToLocal("minetunes.gui.showHud_" + MTConfig.hudMode);
        moveHudButton.enabled = MTConfig.hudMode > 0;
        playModeButton.displayString = StatCollector.translateToLocal("minetunes.gui.playMode" + MTConfig.playMode);
        timeDisplayModeButton.displayString = StatCollector
            .translateToLocal("minetunes.gui.timeDisplayMode" + MTConfig.timeDisplayMode);

        searchField.drawTextBox();
        if (searchField.getText()
            .isEmpty()) {
            String s = StatCollector.translateToLocal("minetunes.gui.search");
            mc.fontRenderer.drawString(
                s,
                searchField.xPosition + searchField.width - mc.fontRenderer.getStringWidth(s) - 5,
                searchField.yPosition + 6,
                0xAAAAAA);
        }

        boolean hasPlaylist = getSelectedPlaylist() != null;
        deletePlaylistButton.visible = reloadPlaylistButton.visible = hasPlaylist;
        deletePlaylistButton.enabled = reloadPlaylistButton.enabled = isShiftKeyDown();
        if (hasPlaylist) {
            drawBox(8, height - 40, 184, 42);
            String s = StatCollector.translateToLocal("minetunes.gui.shiftToActivate");
            int sWidth = mc.fontRenderer.getStringWidth(s);
            drawCenteredString(fontRendererObj, s, 100, height - 36, isShiftKeyDown() ? 0xFFFFFF : 0x666666);
        }

        super.drawScreen(mx, my, partialTicks);
    }

    @Override
    protected void keyTyped(char c, int i) {
        if (MineTunes.playlistCreatorThread == null) playlistNameField.textboxKeyTyped(c, i);

        String searchKey = searchField.getText();
        searchField.textboxKeyTyped(c, i);
        if (!searchField.getText()
            .equals(searchKey)) updateVisibleSongs();
        super.keyTyped(c, i);
    }

    @Override
    protected void mouseClicked(int b, int x, int y) {
        if (MineTunes.playlistCreatorThread == null) playlistNameField.mouseClicked(b, x, y);

        searchField.mouseClicked(b, x, y);
        songPositionSlider.mousePressed(Minecraft.getMinecraft(), x, y);

        super.mouseClicked(b, x, y);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
    }

    public void updateVisibleSongs() {
        visibleSongs.clear();
        Playlist playlist = getSelectedPlaylist();
        String searchKey = searchField.getText()
            .toLowerCase();

        if (playlist != null) for (MP3Metadata meta : playlist.metadataList) if (meta.title.toLowerCase()
            .contains(searchKey)
            || meta.artist.toLowerCase()
                .contains(searchKey)
            || meta.album.toLowerCase()
                .contains(searchKey))
            visibleSongs.add(meta);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        boolean configChanged = false;

        String playlistName = selectedPlaylist >= PlaylistList.playlistNames.size() ? ""
            : PlaylistList.playlistNames.get(selectedPlaylist);
        File playlistFile = PlaylistList.playlists.containsKey(playlistName)
            ? PlaylistList.playlists.get(playlistName).file
            : null;

        switch (button.id) {
            case 0 -> mc.displayGuiScreen(null);
            case 1 -> mc.displayGuiScreen(new GuiDevTools());
            case 2 -> {
                MTConfig.hudMode = MTConfig.hudMode == 2 ? 0 : MTConfig.hudMode + 1;
                configChanged = true;
            }
            case 3 -> mc.displayGuiScreen(new GuiMoveHUD());
            case 4 -> {
                MTConfig.playMode = MTConfig.playMode == 3 ? 0 : MTConfig.playMode + 1;
                configChanged = true;
            }
            case 5 -> {
                String name = playlistNameField.getText();
                playlistNameField.setText("");
                playlistSlot.resetScroll();
                new FileSelector(
                    PlaylistFilter.instance,
                    JFileChooser.FILES_AND_DIRECTORIES,
                    ActionMakePlaylist.instance.withName(name));
            }
            case 6 -> {
                if (currentPlaylist == selectedPlaylist && MineTunes.musicPlayerThread != null)
                    MineTunes.musicPlayerThread.resetPlayer();
                PlaylistList.playlistNames.remove(selectedPlaylist);
                PlaylistList.playlists.remove(playlistName);
                selectedPlaylist = 0;
                PlaylistList.findCompoundAndWrite();
            }
            case 7 -> {
                actionPerformed(deletePlaylistButton);
                ActionMakePlaylist.instance.withName(playlistName)
                    .select(playlistFile);
            }
            case 8 -> {
                MTConfig.timeDisplayMode = MTConfig.timeDisplayMode == 1 ? 0 : MTConfig.timeDisplayMode + 1;
                configChanged = true;
            }
            case 9 -> {
                if (MineTunes.musicPlayerThread != null) {
                    float newFraction = (float) songPositionSlider.sliderValue;
                    MineTunes.musicPlayerThread.seekTo(newFraction);
                }
            }
            case 10 -> {
                if (MineTunes.musicPlayerThread != null) {
                    MineTunes.musicPlayerThread.pauseOrPlay();
                }
            }
        }

        if (configChanged) MTConfig.findCompoundAndWrite();
    }

    public int getTopSize() {
        return 80;
    }

    public void selectPlaylist(int playlist) {
        selectedPlaylist = playlist;
        selectedSong = null;
        musicSlot.resetScroll();
        updateVisibleSongs();
    }

    public int getSelectedPlaylistIndex() {
        return selectedPlaylist;
    }

    public void selectSong(MP3Metadata song) {
        selectedSong = song;
    }

    public MP3Metadata getSelectedSong() {
        return selectedSong;
    }

    public Playlist getSelectedPlaylist() {
        return getPlaylist(getSelectedPlaylistIndex());
    }

    public static void selectCurrentPlaylist(int playlist, int song) {
        currentPlaylist = playlist;
        currentSong = song;
        if (MineTunes.musicPlayerThread != null) MineTunes.musicPlayerThread.onPlaylistChange();
    }

    public static int getCurrentPlaylistIndex() {
        return currentPlaylist;
    }

    public static void selectCurrentSong(int song) {
        currentSong = song;
    }

    public static int getCurrentSong() {
        return currentSong;
    }

    public static Playlist getCurrentPlaylist() {
        return getPlaylist(getCurrentPlaylistIndex());
    }

    public static Playlist getPlaylist(int index) {
        if (index >= PlaylistList.playlistNames.size()) return null;

        String name = PlaylistList.playlistNames.get(index);
        return PlaylistList.playlists.get(name);
    }

}
