/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.jme3.shader.VarType;

/**
 * Represents the gui to manipulate the default argument to a {@link GlslVar}.
 * 
 * @author codex
 */
public class Argument {
    
    private final InputSocket socket;
    
    public Argument(InputSocket socket) {
        validate(socket.getVariable());
        this.socket = socket;
    }
    
    public String fetchValue() {
        return null;
    }
    
    private static void validate(GlslVar var) {
        if (!var.isInput() || var.getDefault() == null) {
            throw new IllegalArgumentException("GlslVar does not have a default!");
        }
    }
    
}
