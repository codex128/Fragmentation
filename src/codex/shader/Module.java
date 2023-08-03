/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.simsilica.lemur.Container;
import java.util.ArrayList;

/**
 *
 * @author codex
 */
public class Module extends Container {
    
    private GLSL glsl;
    private ArrayList<InputSocket> inputs = new ArrayList<>();
    private ArrayList<OutputSocket> outputs = new ArrayList<>();
    
    public Module(GLSL glsl) {
        this.glsl = glsl;
        createSockets();
    }
    
    private void createSockets() {
        glsl.getInputVariables().forEach(v -> inputs.add(new InputSocket(this, v)));
        glsl.getOutputVariables().forEach(v -> outputs.add(new OutputSocket(this, v)));
    }
    
    public GLSL getGlsl() {
        return glsl;
    }
    public ArrayList<InputSocket> getInputSockets() {
        return inputs;
    }
    public ArrayList<OutputSocket> getOutputSockets() {
        return outputs;
    }
    
}
