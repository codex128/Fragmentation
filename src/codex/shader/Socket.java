/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author codex
 */
public abstract class Socket extends Node {
    
    public enum IO {
        Input, Output;
    }
    
    protected final Module module;
    protected final GlslVar variable;
    protected final IO type;
    protected final ArrayList<Connection> connections = new ArrayList<>();
    
    public Socket(Module module, GlslVar variable, IO type) {
        this.module = module;
        this.variable = variable;
        this.type = type;
    }
    
    public boolean acceptConnectionTo(Socket socket) {
        return variable.getType().equals(socket.getVariable().getType()) && type != socket.getType();
    }
    public Connection connect(Socket socket) {
        var connection = new Connection(this, socket);
        connections.add(connection);
        socket.connections.add(connection);
        return connection;
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
    
}
