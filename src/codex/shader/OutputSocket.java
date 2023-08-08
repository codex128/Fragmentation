/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.jme3.math.Vector3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.ElementId;

/**
 *
 * @author codex
 */
public class OutputSocket extends Socket {
    
    public static final String ELEMENT_ID = "output";
    
    public OutputSocket(Module module, GlslVar variable) {
        super(module, variable, Socket.IO.Output, new ElementId(Socket.ELEMENT_ID).child(ELEMENT_ID));
        validate(variable);
    }
    
    @Override
    public void initGui() {
        layout.addChild(0, 0, new Label(variable.getName()));
        layout.addChild(0, 1, hub);
        //hub.setLocalTranslation(50f, 0f, 0f);
    }
    
    private static void validate(GlslVar var) {
        if (!var.isOutput()) {
            throw new IllegalArgumentException("GlslVar is not an output variable!");
        }
    }
    
}
