/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.shader.gui.FragmentationStyle;
import codex.shader.gui.SocketHub;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.style.ElementId;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public abstract class Socket extends Container {
    
    public static final String ELEMENT_ID = "socket";
    public enum IO {
        Input, Output;
    }
    
    protected final Module module;
    protected final GlslVar variable;
    protected final IO type;
    protected final ArrayList<Connection> connections = new ArrayList<>();
    protected SpringGridLayout layout;
    protected SocketHub hub;
    
    public Socket(Module module, GlslVar variable, IO type) {
        this(module, variable, type, new ElementId(ELEMENT_ID));
    }
    public Socket(Module module, GlslVar variable, IO type, ElementId id) {
        super(id);
        this.module = module;
        this.variable = variable;
        this.type = type;
        initBasicGui();
    }
    
    private void initBasicGui() {
        layout = new SpringGridLayout();
        setLayout(layout);
        hub = new SocketHub(this, FragmentationStyle.getTypeColor(variable.getType()));
        //layout.addChild(0, 0, hub);
        hub.addControl(new MouseEventControl(module.getProgram().getConnectorInterface()));
        //layout.addChild(0, 1, new Label(variable.getName()));
    }
    public abstract void initGui();
    
    public Vector3f getConnectionLocation() {
        return hub.getPortLocation().add(0f, -18f, 0f);
    }
    public boolean acceptConnectionTo(Socket socket) {
        return varTypeCompatible(socket) && type != socket.getType() && module != socket.getModule();
    }
    public boolean varTypeCompatible(Socket socket) {
        return varTypeMatch(socket) || variable.isGeneric() || socket.getVariable().isGeneric();
    }
    public boolean varTypeMatch(Socket socket) {
        return variable.getType().equals(socket.getVariable().getType());
    }
    public Connection connect(Socket socket) {
        var connection = new Connection(this, socket);
        connections.add(connection);
        socket.connections.add(connection);
        return connection;
    }
    public void removeConnection(Connection connect) {
        connections.remove(connect);
    }
    
    public void terminate() {
        var list = new LinkedList<Connection>(connections);
        connections.clear();
        for (var c : list) {
            c.terminate();
        }
        list.clear();
    }
    public void setVariableDefault(String def) {
        variable.setDefault(def);
    }
    
    public IO getType() {
        return type;
    }
    public SocketHub getHub() {
        return hub;
    }
    public Module getModule() {
        return module;
    }
    public GlslVar getVariable() {
        return variable;
    }
    public int getNumConnections() {
        return connections.size();
    }
    public ArrayList<Connection> getConnectionList() {
        return connections;
    }
    @Override
    public String toString() {
        return module.getId()+"-"+variable.getName();
    }
    
}
