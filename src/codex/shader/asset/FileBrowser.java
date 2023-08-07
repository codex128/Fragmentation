/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.asset;

import codex.boost.GameAppState;
import codex.shader.Program;
import com.jme3.app.Application;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author codex
 */
public class FileBrowser extends GameAppState {
    
    public static final String HOME = System.getProperty("user.home");
    public static final String LOCALSTORAGE = HOME+"/FragmentationPlatform";
    
    @Override
    protected void init(Application app) {
        var storage = new File(LOCALSTORAGE);
        if (!storage.exists()) {
            storage.mkdir();
        }
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    public void importFile(File start, String... extensions) {
        // opens the file browser in import mode
    }
    public void exportFile(File start, String name, String extension, Exporter export) {
        // opens the file browser in export mode
    }
    
    public static String path(String... sections) {
        var render = new StringBuilder();
        int i = 0;
        for (var p : sections) {
            render.append(p);
            if (++i < sections.length) {
                render.append("/");
            }
        }
        return render.toString();
    }
    
}
