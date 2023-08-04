/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.shader.gui.Argument;

/**
 *
 * @author codex
 */
public class InputSocket extends Socket {
    
    private Argument argument;
    
    public InputSocket(Module module, GlslVar var) {
        super(module, var, Socket.IO.Input);
        validate(var);
        if (var.getDefault() != null) {
            argument = Argument.create(this);
        }
        initGui();
    }    
    
    private void initGui() {
        layout.addChild(0, 1, argument);
    }
    
    @Override
    public boolean acceptConnectionTo(Socket socket) {
        return super.acceptConnectionTo(socket) && connections.isEmpty();
    }
    public Connection getConnection() {
        return !connections.isEmpty() ? connections.get(0) : null;
    }
    public Argument getArgument() {
        return argument;
    }
    
    public static InputSocket create(Module module, GlslVar var) {
        if (var.getType() == null || !var.getType().equals("String")) {
            return new InputSocket(module, var);
        }
        else {
            return new TerminalInputSocket(module, var);
        }
    }
    private static void validate(GlslVar var) {
        if (!var.isInput()) {
            throw new IllegalArgumentException("GlslVar is not an input variable!");
        }
    }
    
}
