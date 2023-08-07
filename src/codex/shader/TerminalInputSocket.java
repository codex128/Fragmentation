/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.simsilica.lemur.Label;

/**
 *
 * @author codex
 */
public class TerminalInputSocket extends InputSocket {
    
    public TerminalInputSocket(Module module, GlslVar var) {
        super(module, var);
    }
    
    @Override
    public void initGui() {
        //layout.addChild(0, 0, hub);
        layout.addChild(0, 1, new Label(variable.getName()));
        layout.addChild(0, 2, argument);
    }
    @Override
    public boolean acceptConnectionTo(Socket socket) {
        return false;
    }
    
}
