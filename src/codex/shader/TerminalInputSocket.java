/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

/**
 *
 * @author codex
 */
public class TerminalInputSocket extends InputSocket {
    
    public TerminalInputSocket(Module module, GlslVar var) {
        super(module, var);
    }
    
    @Override
    public boolean acceptConnectionTo(Socket socket) {
        return false;
    }
    
}
