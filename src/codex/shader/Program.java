/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.boost.GameAppState;
import codex.shader.gui.PartialConnection;
import codex.shader.gui.SocketHub;
import com.jme3.app.Application;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.event.CursorButtonEvent;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.CursorListener;
import com.simsilica.lemur.event.CursorMotionEvent;
import com.simsilica.lemur.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private GeneralInput input;
    private SocketConnector connector;
    
    public Program() {}
    
    @Override
    protected void init(Application app) {
        
        // background quad which is used to track cursor location in the world.
        var background = assetManager.loadModel("Models/background.j3o");
        background.setLocalScale(2000f);
        var mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.DarkGray);
        background.setMaterial(mat);
        background.addControl(new CursorEventControl(input));
        scene.attachChild(background);
        
        // the socket connector, which listens to mouse events on socket hubs
        connector = new SocketConnector();
        
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    public void load(InputStream stream) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("nextId=")) {
                Module.setNextId(Long.parseLong(line.substring("nextId=".length())));
            }
            else if (line.startsWith("module{")) {
                var args = line.substring("module{".length(), line.length()-1).split(",");
                long id = -1;
                String glslPath = null;
                var mandatory = false;
                for (var arg : args) {
                    var a = arg.split("=", 2);
                    switch (a[0]) {
                        case "id" -> id = Long.parseLong(a[1]);
                        case "glsl" -> glslPath = a[1];
                        case "mandatory" -> mandatory = Boolean.parseBoolean(a[1]);
                    }
                }
                if (id < 0) {
                    throw new NullPointerException("Module ID not specified!");
                }
                if (glslPath == null) {
                    throw new NullPointerException("GLSL file path is not defined!");
                }
                var glsl = (GLSL)assetManager.loadAsset(glslPath);
                var module = new Module(this, glsl);
                module.setIsMandatory(mandatory);
                scene.attachChild(module);
                modules.add(module);
            }
            else if (line.startsWith("connection{")) {
                var args = line.substring("connection{".length(), line.length()-1).split(",");
                String out = null, in = null;
                for (var arg : args) {
                    if (arg.startsWith("out=")) {
                        out = arg.substring("out=".length());
                    }
                    else if (arg.startsWith("in=")) {
                        in = arg.substring("in=".length());
                    }
                }
                if (out == null || in == null) {
                    throw new NullPointerException("Insufficient data to make a connection!");
                }
                
            }
        }
    }
    public void createNewProgram() {
        
    }
    
    private Socket getSocketByAddress(String address) {
        var args = address.split("-", 2);
        if (args.length < 2) {
            throw new NullPointerException("Insufficient information to locate socket!");
        }
        long id = Long.parseLong(args[0]);
        var module = getModuleById(id);
        if (module == null) {
            throw new NullPointerException("Module ID#"+id+" does not exist!");
        }
        var socket = module.getSocketByVariableName(args[1]);
        if (socket == null) {
            throw new NullPointerException("Socket");
        }
    }
    private Module getModuleById(long id) {
        return modules.stream().filter(m -> m.getId() == id).findAny().orElse(null);
    }
    
    public Collection<Module> getModules() {
        return modules;
    }
    public Module getOutputModule() {
        return output;
    }
    public SocketConnector getSocketConnector() {
        return connector;
    }
    
    public class GeneralInput implements CursorListener {
        
        @Override
        public void cursorButtonEvent(CursorButtonEvent event, Spatial target, Spatial capture) {
            if (!event.isPressed()) {
                connector.terminate();
            }
        }
        @Override
        public void cursorEntered(CursorMotionEvent event, Spatial target, Spatial capture) {}
        @Override
        public void cursorExited(CursorMotionEvent event, Spatial target, Spatial capture) {}
        @Override
        public void cursorMoved(CursorMotionEvent event, Spatial target, Spatial capture) {
            if (connector.connection != null) {
                connector.connection.setCursorLocation(event.getCollision().getContactPoint());
            }
        }
        
    }
    public class SocketConnector implements MouseListener {
        
        PartialConnection connection;
        Material mat;
        
        public SocketConnector() {
            mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded");
            mat.setColor("Color", ColorRGBA.Blue);
        }
        
        @Override
        public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
            if (event.isPressed() && target instanceof SocketHub) {
                var hub = (SocketHub)target;
                if (hub.getSocket().getType() == Socket.IO.Input && hub.getSocket().getNumConnections() > 0) {
                    var c = hub.getSocket().getConnectionList().get(0);
                    connection = new PartialConnection(c.getOutputSocket().getHub());
                    c.terminate();
                }
                else {
                    connection = new PartialConnection(hub);
                }
                connection.setMaterial(mat);
                scene.attachChild(connection);
                event.setConsumed();
            }
            else if (event.isReleased() && connection != null && target != connection.getConnectedHub() && target instanceof SocketHub) {
                var hub = (SocketHub)target;
                if (hub.getSocket().getType() != connection.getConnectedHub().getSocket().getType()
                        && hub.getSocket().acceptConnectionTo(connection.getConnectedHub().getSocket())
                        && connection.getConnectedHub().getSocket().acceptConnectionTo(hub.getSocket())) {
                    var c = hub.getSocket().connect(connection.getConnectedHub().getSocket());
                    scene.attachChild(c.createLineGeometry(mat));
                }
                terminate();
                event.setConsumed();
            }
        }
        @Override
        public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {}
        @Override
        public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {}
        @Override
        public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {}
        
        public void terminate() {
            if (connection == null) return;
            connection.removeFromParent();
            connection = null;
        }
        
    }
    
}
