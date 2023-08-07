/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.boost.GameAppState;
import codex.shader.asset.FileBrowser;
import com.jme3.app.Application;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.simsilica.lemur.core.VersionedList;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author codex
 */
public class ShaderNodeManager extends GameAppState {
    
    public static final String ADDONS = FileBrowser.LOCALSTORAGE+"/addons";
    public static final String INDEX = "Templates/native-nodes.index";
    public static final String NATIVES = "Shaders";
    
    private VersionedList<GLSL> nodes = new VersionedList<>();
    
    @Override
    protected void init(Application app) {
        loadNatives(INDEX);
        var addons = new File(ADDONS);
        if (!addons.exists()) {
            addons.mkdir();
        }
        else try {
            loadAddons(addons);
        } catch (IOException ex) {
            Logger.getLogger(ShaderNodeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    private void loadNatives(String indexPath) {
        var index = (ArrayList<String>)assetManager.loadAsset(indexPath);
        for (var i : index) {
            var glsl = (GLSL)assetManager.loadAsset(FileBrowser.path(NATIVES, i));
            glsl.setAssetLocator(GLSL.NATIVE, i);
            nodes.add(glsl);
        }
    }
    private void loadAddons(File folder) throws IOException {
        if (!folder.exists()) {
            throw new NullPointerException("Addon folder does not exist!");
        }
        var files = folder.listFiles((File dir, String name) -> name.endsWith(".sn"));
        for (var f : files) {
            var glsl = new GLSL(f);
            glsl.setAssetLocator(GLSL.ADDON, f.getName());
            nodes.add(glsl);
        }
    }
    
    public VersionedList<GLSL> getNodes() {
        return nodes;
    }
    
    public static class IndexLoader implements AssetLoader {
        @Override
        public ArrayList<String> load(AssetInfo assetInfo) throws IOException {
            var index = new ArrayList<String>();
            var reader = new BufferedReader(new InputStreamReader(assetInfo.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                index.add(line);
            }
            return index;
        }        
    }
    
}
