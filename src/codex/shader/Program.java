/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.boost.GameAppState;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author codex
 */
public class Program extends GameAppState {
    
    private Node scene;
    private ArrayList<Module> modules = new ArrayList<>();
    private Module output;
    private Connector connector;
    
    public Collection<Module> getModules() {
        return modules;
    }
    public Module getOutputModule() {
        return output;
    }
    public Connector getConnectorMouse() {
        return connector;
    }

    @Override
    protected void initialize(Application app) {
        var mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded");
        mat.setColor("Color", ColorRGBA.Blue);
        connector = new Connector(mat);
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    public class Connector implements MouseListener {
        
        Connection recent;
        Material mat;
        
        public Connector(Material mat) {
            this.mat = mat;
        }
        
        @Override
        public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
            if (event.isPressed()) {
                event.setConsumed();
            }
            else {
                recent = null;
            }
        }
        @Override
        public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {
            if (capture != null && target != capture) {
                var s1 = (Socket)capture.getParent();
                var s2 = (Socket)target.getParent();
                if (s1.acceptConnectionTo(s2)) {
                    if (recent != null) recent.terminate();
                    recent = s1.connect(s2);
                    var line = recent.createLineGeometry(mat);
                    line.setQueueBucket(RenderQueue.Bucket.Gui);
                    scene.attachChild(line);
                }
            }
        }
        @Override
        public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {
            if (capture != null && capture instanceof InputSocket) {
                
            }
        }
        @Override
        public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {}
        
    }
    
}
