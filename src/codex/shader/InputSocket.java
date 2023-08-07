/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.shader.gui.Argument;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.ElementId;

/**
 *
 * @author codex
 */
public class InputSocket extends Socket {
    
    public static final String ELEMENT_ID = "input";
    protected Argument argument;
    
    public InputSocket(Module module, GlslVar var) {
        super(module, var, Socket.IO.Input, new ElementId(Socket.ELEMENT_ID).child(ELEMENT_ID));
        validate(var);
        createArgument();
    }    
    
    private boolean createArgument() {
        if (argument != null && variable.getDefault() != null) {
            return false;
        }
        argument = Argument.create(this);
        return true;
    }
    
    @Override
    public void initGui() {
        layout.addChild(0, 0, hub);
        layout.addChild(0, 1, new Label(variable.getName()));
        layout.addChild(0, 2, argument);
    }    
    @Override
    public boolean acceptConnectionTo(Socket socket) {
        return super.acceptConnectionTo(socket) && connections.isEmpty();
    }
    @Override
    public void setVariableDefault(String def) {
        assert def != null;
        variable.setDefault(def);
        createArgument();
        argument.displayValue(def);
    }
    
    public Connection getConnection() {
        return !connections.isEmpty() ? connections.get(0) : null;
    }
    public Argument getArgument() {
        return argument;
    }
    
    public static InputSocket create(Module module, GlslVar var) {
        if (var.getType() == null || (!var.getType().equals("String") && !var.getType().equals("generic"))) {
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
