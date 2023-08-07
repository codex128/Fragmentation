/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.boost.ColorHSBA;
import codex.boost.GameAppState;
import codex.shader.asset.FileBrowser;
import codex.shader.asset.ProgramAsset;
import codex.shader.compile.*;
import codex.shader.compile.Compiler;
import codex.shader.input.SocketConnectorInterface;
import com.jme3.app.Application;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.VersionedList;
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
public class Program extends GameAppState {
    
    private ShaderNodeManager nodes;
    private Container background;
    private File file;
    private Node scene = new Node("program-gui");
    private Node moduleScene = new Node("module-gui");
    private ArrayList<Module> modules = new ArrayList<>();
    private Module output;
    private Module selected;
    private KeyHandler keys;
    private MouseHandler mouse;
    private SocketConnectorInterface connectInterface;
    
    public Program() {}
    
    @Override
    protected void init(Application app) {        
        
        nodes = getState(ShaderNodeManager.class, true);
        
        initScene();
        initGui();     
        
        try {
            createFromAsset("Templates/testProgram.fnp");
        } catch (IOException ex) {
            Logger.getLogger(FileBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
    
    private void initScene() {
        
        scene.attachChild(moduleScene);
        moduleScene.setLocalTranslation(windowSize.x/2, windowSize.y/2, 0);
        
        // background quad which is used to track cursor location in the world.
        background = new Container();
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
    private void initGui() {        
        
        var modulePick = new Container();
        modulePick.setLocalTranslation(100f, windowSize.y-100f, 0f);
        var list = modulePick.addChild(new ListBox<GLSL>());
        list.setModel(nodes.getNodes());
        list.setVisibleItems(10);
        list.setPreferredSize(new Vector3f(150f, 200f, 0f));
        var options = modulePick.addChild(new Container());
        options.setLayout(new BoxLayout(Axis.X, FillMode.Even));
        options.addChild(new Button("Add")).addClickCommands((Button source) -> {
            var m = addModule(list.getSelectedItem());
            m.setLocalTranslation(windowSize.divide(2).subtractLocal(m.getParent().getWorldTranslation()));
            GuiGlobals.getInstance().getPopupState().closePopup(modulePick);
        });
        options.addChild(new Button("Cancel")).addClickCommands(new PopupCommand(modulePick));
        
        var menubar = new Container();
        menubar.setLayout(new BoxLayout(Axis.X, FillMode.None));
        scene.attachChild(menubar);
        final float height = 30f;
        menubar.setLocalTranslation(0f, height, 0f);
        menubar.setPreferredSize(new Vector3f(windowSize.x, height, 0f));
        menubar.addChild(new Button("Add")).addClickCommands(new PopupCommand(modulePick, new ColorRGBA(0f, 0f, 0f, .7f)));
        var export = menubar.addChild(new Button("Export"));
        export.addClickCommands(new ExportCommand());
        
    }
    
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
                        +"glsl="+m.getGlsl().getAssetLocator()[0]+":"+m.getGlsl().getAssetLocator()[1]+";"
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
    public void export(File target) {
        if (!target.getName().endsWith(".frag")) {
            throw new IllegalArgumentException("Only exports to .frag files!");
        }
        var compiler = new Compiler(this);
        var thread = new Thread(compiler);
        thread.start();
    }
    
    private void parseModuleData(String line) {
        var args = line.substring("module{".length(), line.length()-1).split(";");
        long id = -1;
        String[] locator = null;
        var position = new Vector3f();
        //var isOutput = false;
        var defaults = new ArrayList<String[]>();
        for (var arg : args) {
            var a = arg.split("=", 2);
            switch (a[0]) {
                case "id" -> id = Long.parseLong(a[1]);
                case "glsl" -> locator = a[1].split(":", 2);
                case "position" -> position = parseGuiPositionVector(a[1]);
                //case "isOutput" -> isOutput = Boolean.parseBoolean(a[1]);
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
        if (locator == null || locator.length != 2) {
            throw new NullPointerException("GLSL locator is not defined!");
        }
        var module = new Module(this, GLSL.fromLocator(assetManager, locator), id);
        module.setLocalTranslation(position);
        for (var def : defaults) {
            def[0] = def[0].substring(5);
            module.getGlsl().applyDefault(def);
        }
        if (addModule(module) && output == null && module.getGlsl().isOutput()) {
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
    private void export(Compiler compiler, File target) {
        try {
            if (target.exists()) target.delete();
            target.createNewFile();
            try (var writer = new FileWriter(target)) {
                for (var line : compiler.getCompiledCode()) {
                    writer.write(line+"\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private File fetchTargetFile() {
        String parent;
        if (file != null) parent = file.getParent();
        else parent = FileBrowser.HOME;
        return new File(FileBrowser.path(parent, "export.frag"));
    }
    
    private Container createPopup(Vector3f location, String title, String body) {
        var popup = new Container();
        popup.setLocalTranslation(location);
        if (title != null) popup.addChild(new Label(title));
        if (body != null) popup.addChild(new Label(body));
        return popup;
    }
    
    public Module addModule(GLSL source) {
        var m = new Module(this, GLSL.fromLocator(assetManager, source.getAssetLocator()));
        if (addModule(m)) return m;
        else return null;
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
    
    public void setSelected(Module m) {
        selected = m;
    }
    public Module getSelected() {
        return selected;
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
            if (selected != null && evt.isPressed()
                    && (evt.getKeyCode() == KeyInput.KEY_X || evt.getKeyCode() == KeyInput.KEY_DELETE)) {
                selected.terminate();
                selected = null;
            }
        }
    }
    public class MouseHandler implements CursorListener {
        
        private Vector3f drag;
        
        @Override
        public void cursorButtonEvent(CursorButtonEvent event, Spatial target, Spatial capture) {
            if (event.isPressed()) {
                if (event.getButtonIndex() == MouseInput.BUTTON_MIDDLE) {
                    event.setConsumed();
                }
                else if (event.getButtonIndex() == MouseInput.BUTTON_LEFT) {
                    selected = null;
                }
            }
            else if (!event.isPressed()) {
                connectInterface.terminate();
                drag = null;
            }
        }
        @Override
        public void cursorEntered(CursorMotionEvent event, Spatial target, Spatial capture) {}
        @Override
        public void cursorExited(CursorMotionEvent event, Spatial target, Spatial capture) {}
        @Override
        public void cursorMoved(CursorMotionEvent event, Spatial target, Spatial capture) {
            if (capture == background) {
                var current = event.getCollision().getContactPoint();
                if (drag == null) {
                    drag = new Vector3f(current);
                }
                moduleScene.move(current.subtract(drag));
                drag.set(current);
            }
            connectInterface.setCursorLocation(event.getCollision().getContactPoint());
        }
        
    }
    private class PopupCommand implements Command<Button> {
        
        Spatial popup;
        ColorRGBA background;
        
        public PopupCommand(Spatial popup, ColorRGBA background) {
            this.popup = popup;
            this.background = background;
        }
        public PopupCommand(Spatial popup) {
            
        }
        
        @Override
        public void execute(Button source) {
            if (background != null) {
                GuiGlobals.getInstance().getPopupState().showModalPopup(popup, background);
            }
            else {
                GuiGlobals.getInstance().getPopupState().closePopup(popup);
            }
        }
        
    }
    private class ExportCommand implements Command<Button>, CompileListener {
        
        Container waiting;
        
        @Override
        public void execute(Button source) {
            if (waiting != null) return;
            var compiler = new Compiler(Program.this);
            compiler.addListener(this);
            getState(CompileState.class, true).compile(compiler);
            waiting = createPopup(windowSize.mult(.4f), "Compiling...", null);
            GuiGlobals.getInstance().getPopupState().showModalPopup(waiting, new ColorRGBA(0f, 0f, 0f, .5f));
        }
        @Override
        public void compileError(CompilingError error) {
            GuiGlobals.getInstance().getPopupState().closePopup(waiting);
            waiting = null;
            GuiGlobals.getInstance().getPopupState().showPopup(createPopup(windowSize.mult(.4f), "Compile Error", error.getErrorMessage()));
        }
        @Override
        public void compileFinished(Compiler compiler) {
            GuiGlobals.getInstance().getPopupState().closePopup(waiting);
            waiting = null;
            export(compiler, fetchTargetFile());
        }
        
    }
    
}
