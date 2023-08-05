/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.shader.gui.SocketHub;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.MouseEventControl;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public abstract class Socket extends Container {
    
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
        super("");
        this.module = module;
        this.variable = variable;
        this.type = type;
        initGui();
    }
    
    private void initGui() {
        layout = new SpringGridLayout();
        setLayout(layout);
        layout.addChild(0, 0, new Label(variable.getName()));
        hub = new SocketHub(this, ColorRGBA.Red);
        attachChild(hub);
        hub.addControl(new MouseEventControl(module.getProgram().getConnectorInterface()));
    }
    
    public Vector3f getConnectionLocation() {
        return hub.getPortLocation();
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
    
    public Module getModule() {
        return module;
    }
    public GlslVar getVariable() {
        return variable;
    }
    public IO getType() {
        return type;
    }
    public ArrayList<Connection> getConnectionList() {
        return connections;
    }
    public int getNumConnections() {
        return connections.size();
    }
    public SocketHub getHub() {
        return hub;
    }
    @Override
    public String toString() {
        return module.getId()+"-"+variable.getName();
    }
    
}
