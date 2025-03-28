package vazkii.minetunes.player.chooser.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import net.minecraft.util.StatCollector;

public class Filter extends FileFilter implements java.io.FileFilter {

    String desc;
    String accept;

    public Filter(String name, String accept) {
        desc = StatCollector.translateToLocal(name);
        this.accept = accept;
    }

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName()
            .endsWith(accept);
    }

    @Override
    public String getDescription() {
        return desc;
    }

}
