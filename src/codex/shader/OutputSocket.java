/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

/**
 *
 * @author codex
 */
public class OutputSocket extends Socket {
    
    public OutputSocket(Module module, GlslVar variable) {
        super(module, variable, Socket.IO.Output);
        validate(variable);
    }
    
    private static void validate(GlslVar var) {
        if (!var.isOutput()) {
            throw new IllegalArgumentException("GlslVar is not an output variable!");
        }
    }
    
}
