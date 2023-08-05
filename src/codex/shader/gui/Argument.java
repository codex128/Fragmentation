/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.shader.GlslVar;
import codex.shader.InputSocket;
import com.simsilica.lemur.Container;

/**
 * Represents the gui to manipulate the default argument to a {@link GlslVar}.
 * 
 * @author codex
 */
public abstract class Argument extends Container {
    
    private final InputSocket socket;
    
    public Argument(InputSocket socket) {
        validate(socket.getVariable());
        this.socket = socket;
    }
    
    public void applyToDefault() {
        socket.getVariable().setDefault(getDefaultValue());
    }
    
    public abstract void displayValue(String value);
    public abstract String getDefaultValue();
    public void compile(GlslVar var) {
        var.setDefault(getDefaultValue());
    }
    
    public static Argument create(InputSocket socket) {
        var arg = make(socket);
        if (arg != null) arg.displayValue(socket.getVariable().getDefault());
        return arg;
    }
    private static Argument make(InputSocket socket) {
        var type = socket.getVariable().getType();
        switch (type) {
            case "float" -> {
                return new FloatArgument(socket);
            }
            case "int" -> {
                if (socket.getVariable().getDefaultProperties() != null) {
                    return new StateArgument(socket);
                }
                else {
                    return new IntegerArgument(socket);
                }
            }
            case "String" -> {
                return new StringArgument(socket);
            }
            default -> {
                return null;
            }
        }
    }
    private static void validate(GlslVar var) {
        if (!var.isInput() || var.getDefault() == null) {
            throw new IllegalArgumentException("GlslVar does not have a default!");
        }
    }
    
}
