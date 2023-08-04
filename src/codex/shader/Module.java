/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.component.BoxLayout;
import java.util.ArrayList;

/**
 *
 * @author codex
 */
public class Module extends Container {
    
    private final Program program;
    private final GLSL glsl;
    private ArrayList<InputSocket> inputs = new ArrayList<>();
    private ArrayList<OutputSocket> outputs = new ArrayList<>();
    
    public Module(Program program, GLSL glsl) {
        this.program = program;
        this.glsl = glsl;
        createSockets();
    }
    
    private void createSockets() {
        glsl.getInputVariables().forEach(v -> inputs.add(new InputSocket(this, v)));
        glsl.getOutputVariables().forEach(v -> outputs.add(new OutputSocket(this, v)));
        var layout = new BoxLayout(Axis.Y, FillMode.Even);
        setLayout(layout);
        for (var s : outputs) {
            addChild(s);
        }
        for (var s : inputs) {
            addChild(s);
        }
    }
    
    public void terminate() {
        inputs.stream().forEach(s -> s.terminate());
        outputs.stream().forEach(s -> s.terminate());
        removeFromParent();
    }
    
    public Program getProgram() {
        return program;
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
