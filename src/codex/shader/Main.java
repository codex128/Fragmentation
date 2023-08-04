package codex.shader;

import codex.shader.asset.FileBrowser;
import codex.shader.asset.GlslLoader;
import codex.shader.asset.ProgramAsset;
import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.simsilica.lemur.GuiGlobals;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        GuiGlobals.initialize(this);
        
        assetManager.registerLoader(ProgramAsset.class, "fnp");
        assetManager.registerLoader(GlslLoader.class, "sn");
        
        stateManager.attach(new Program());
        stateManager.attach(new FileBrowser());
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
