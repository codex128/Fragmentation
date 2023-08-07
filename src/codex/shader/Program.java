/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.boost.ColorHSBA;
import codex.boost.GameAppState;
import codex.shader.asset.ProgramAsset;
import codex.shader.compile.CompileListener;
import codex.shader.compile.Compiler;
import codex.shader.compile.CompilingError;
import codex.shader.gui.ProgramTool;
import codex.shader.input.SocketConnectorInterface;
import com.jme3.app.Application;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.event.CursorButtonEvent;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.CursorListener;
import com.simsilica.lemur.event.CursorMotionEvent;
import com.simsilica.lemur.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author codex
 */
public class Program extends GameAppState implements CompileListener {
    
    private File file;
    private Node scene = new Node("program-gui");
    private Node moduleScene = new Node("module-gui");
    private ArrayList<Module> modules = new ArrayList<>();
    private ArrayList<Module> selectBuffer = new ArrayList<>();
    private ArrayList<ProgramTool> tools = new ArrayList<>();
    private Module output;
    private KeyHandler keys;
    private MouseHandler mouse;
    private SocketConnectorInterface connectInterface;
    private ProgramTool activeTool;
    
    public Program() {}
    
    @Override
    protected void init(Application app) {        
        
        scene.attachChild(moduleScene);
        moduleScene.setLocalTranslation(windowSize.x/2, windowSize.y/2, 0);
        
        // background quad which is used to track cursor location in the world.
        var background = new Container();
        background.setLocalTranslation(0f, windowSize.y, -10f);
        background.setBackground(new QuadBackgroundComponent(new ColorHSBA(0f, 0f, .01f, 1f).toRGBA()));
        background.setPreferredSize(windowSize);
        background.addControl(new CursorEventControl(mouse = new MouseHandler()));
        scene.attachChild(background);
        
        // the socket connector, which listens to mouse events on socket hubs
        Material lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        lineMat.setColor("Color", ColorRGBA.Blue);
        connectInterface = new SocketConnectorInterface(this);
        connectInterface.setMaterial(lineMat);
        
    }
    @Override
    protected void cleanup(Application app) {
        close();
    }
    @Override
    protected void onEnable() {        
        guiNode.attachChild(scene);
        GuiGlobals.getInstance().addKeyListener(keys = new KeyHandler());
    }
    @Override
    protected void onDisable() {        
        scene.removeFromParent();
        GuiGlobals.getInstance().removeKeyListener(keys);
    }
    @Override
    public void compileError(CompilingError error) {}
    @Override
    public void compileFinished(Compiler compiler) {}
    
    public void load(File file) throws IOException {
        if (!file.getName().endsWith(".fnp")) {
            throw new IllegalArgumentException("Can only load .fnp files!");
        }
        this.file = file;
        load(new FileInputStream(file));
    }
    public void load(InputStream stream) throws IOException {
        close();
        var reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("nextId=")) {
                Module.setNextId(Long.parseLong(line.substring("nextId=".length())));
            }
            else if (line.startsWith("module{")) {
                parseModuleData(line);
            }
            else if (line.startsWith("connection{")) {
                parseConnectionData(line);
            }
        }
    }
    public void createFromAsset(String asset) throws IOException {
        var a = (ProgramAsset)assetManager.loadAsset(asset);
        load(a.getInfo().openStream());
    }
    public void save(File file) throws IOException {
        if (!file.getName().endsWith(".fnp")) {
            throw new IllegalArgumentException("Can only save to .fpn files!");
        }
        if (file.exists()) file.delete();
        file.createNewFile();
        try (var writer = new FileWriter(file)) {
            writer.write("path="+file.getAbsolutePath());
            writer.write("\nnextId="+Module.getNextId());
            for (var m : modules) {
                writer.write("\nmodule{id="+m.getId()+";"
                        +"glsl="+m.getGlsl().getAssetInfo().getKey().getName()+";"
                        +"position="+((int)m.getLocalTranslation().x)+","+((int)m.getLocalTranslation().y));
                for (var s : m.getInputSockets()) {
                    if (s.getVariable().getDefault() == null) continue;
                    writer.write(";def-"+s.getVariable().getName()+"="+s.getVariable().getDefault());
                }
                if (m == output) {
                    writer.write(";isOutput=true");
                }
                writer.write("}");
            }
            for (var m : modules) {
                for (var s : m.getInputSockets()) {
                    if (s.getConnection() == null) continue;
                    writer.write("\nconnection{out="+s.getConnection().getOutputSocket()+";"
                            +"in="+s.getConnection().getInputSocket());
                    writer.write("}");
                }
            }
        }
    }
    public void close() {
        if (modules.isEmpty()) return;
        connectInterface.terminate();
        output = null;
        for (var m : modules) {
            m.terminate();
        }
        modules.clear();
    }
    public void export(File file) {
        if (!file.getName().endsWith(".frag")) {
            throw new IllegalArgumentException("Only exports to .frag files!");
        }
        var compiler = new Compiler(this);
        compiler.addListener(new CompileListener() {
            @Override
            public void compileError(CompilingError error) {}
            @Override
            public void compileFinished(Compiler compiler) {
                try {
                    if (file.exists()) file.delete();
                    file.createNewFile();
                    try (var writer = new FileWriter(file)) {
                        for (var line : compiler.getCompiledCode()) {
                            writer.write(line+"\n");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        var thread = new Thread(compiler);
        thread.start();
    }
    
    private void parseModuleData(String line) {
        var args = line.substring("module{".length(), line.length()-1).split(";");
        long id = -1;
        String glslPath = null;
        var position = new Vector3f();
        var isOutput = false;
        var defaults = new ArrayList<String[]>();
        for (var arg : args) {
            var a = arg.split("=", 2);
            switch (a[0]) {
                case "id" -> id = Long.parseLong(a[1]);
                case "glsl" -> glslPath = a[1];
                case "position" -> position = parseGuiPositionVector(a[1]);
                case "isOutput" -> isOutput = Boolean.parseBoolean(a[1]);
                default -> {
                    if (a[0].startsWith("def-")) {
                        defaults.add(a);
                    }
                }
            }
        }
        if (id < 0) {
            throw new NullPointerException("Module ID not specified!");
        }
        if (glslPath == null) {
            throw new NullPointerException("GLSL file path is not defined!");
        }
        var glsl = (GLSL)assetManager.loadAsset(glslPath);
        var module = new Module(this, glsl, id);
        module.setLocalTranslation(position);
        for (var def : defaults) {
            def[0] = def[0].substring(5);
            module.getGlsl().applyDefault(def);
        }
        if (addModule(module) && isOutput) {
            if (!module.getOutputSockets().isEmpty()) {
                throw new IllegalStateException("The output module cannot have output sockets!");
            }
            output = module;
        }
    }
    private void parseConnectionData(String line) {
        var args = line.substring("connection{".length(), line.length()-1).split(";");
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
            System.err.println("Warning: insufficient data to make a connection");
        }
        if (connect(getSocketByAddress(out), getSocketByAddress(in)) == null) {
            System.err.println("Warning: could not make connection ("+out+" -> "+in+")");
        }
    }
    private Vector3f parseGuiPositionVector(String data) {
        var args = data.split(",", 2);
        if (args.length < 2) {
            throw new NullPointerException("Missing vector data!");
        }
        Vector3f position = new Vector3f();
        position.x = Float.parseFloat(args[0]);
        position.y = Float.parseFloat(args[1]);
        return position;
    }
    private Socket getSocketByAddress(String address) {
        var args = address.split("-", 2);
        if (args.length < 2) {
            throw new NullPointerException("Insufficient information to locate socket!");
        }
        long id = Long.parseLong(args[0]);
        var module = getModuleById(id);
        if (module == null) {
            throw new NullPointerException("Module#"+id+" does not exist!");
        }
        var socket = module.getSocketByVariableName(args[1]);
        if (socket == null) {
            throw new NullPointerException("Socket \""+args[1]+"\" does not exist in "+module+"!");
        }
        return socket;
    }
    private Module getModuleById(long id) {
        return modules.stream().filter(m -> m.getId() == id).findAny().orElse(null);
    }
    
    public boolean addModule(Module m) {
        if (modules.contains(m)) return false;
        moduleScene.attachChild(m);
        modules.add(m);
        return true;
    }
    public boolean removeModule(Module m) {
        if (m == output) return false;
        if (modules.remove(m)) {
            m.terminate();
            m.removeFromParent();
            return true;
        }
        return false;
    }
    public Connection connect(Socket s1, Socket s2) {
        if (!s1.acceptConnectionTo(s2) || !s2.acceptConnectionTo(s1)) {
            return null;
        }
        var connection = s1.connect(s2);
        connection.setMaterial(connectInterface.getMaterial());
        moduleScene.attachChild(connection);
        return connection;
    }
    
    public boolean requestCapture(ProgramTool tool) {
        if (activeTool == null) {
            activeTool = tool;
            return true;
        }
        return false;
    }
    public void releaseCapture(ProgramTool tool) {
        if (tool == activeTool) {
            activeTool = null;
        }
    }
    public boolean captureAvailable() {
        return activeTool == null;
    }
    
    public Node getGuiScene() {
        return moduleScene;
    }
    public Collection<Module> getModules() {
        return modules;
    }
    public Module getOutputModule() {
        return output;
    }
    public SocketConnectorInterface getConnectorInterface() {
        return connectInterface;
    }
    
    public class KeyHandler implements KeyListener {   
        @Override
        public void onKeyEvent(KeyInputEvent evt) {
            tools.stream().forEach(t -> t.onKeyEvent(evt));
            if (evt.isPressed() && evt.getKeyCode() == KeyInput.KEY_SPACE) {
                export(new File("/home/codex/simple.frag"));
            }
        }
    }
    public class MouseHandler implements CursorListener {
        
        @Override
        public void cursorButtonEvent(CursorButtonEvent event, Spatial target, Spatial capture) {
            if (!event.isPressed()) {
                connectInterface.terminate();
            }
        }
        @Override
        public void cursorEntered(CursorMotionEvent event, Spatial target, Spatial capture) {}
        @Override
        public void cursorExited(CursorMotionEvent event, Spatial target, Spatial capture) {}
        @Override
        public void cursorMoved(CursorMotionEvent event, Spatial target, Spatial capture) {
            connectInterface.setCursorLocation(event.getCollision().getContactPoint());
        }
        
    }
    
}
