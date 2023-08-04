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
import java.util.function.Consumer;

/**
 *
 * @author codex
 */
public class Module extends Container {
    
    private static long nextId = 0;
    
    private final long id = nextId++;
    private final Program program;
    private final GLSL glsl;
    private final ArrayList<InputSocket> inputs = new ArrayList<>();
    private final ArrayList<OutputSocket> outputs = new ArrayList<>();
    private boolean mandatory = false;
    
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
    
    public void setIsMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
    public void terminate() {
        inputs.stream().forEach(s -> s.terminate());
        outputs.stream().forEach(s -> s.terminate());
        removeFromParent();
    }
    
    public void forEachSocket(Consumer<Socket> foreach) {
        inputs.forEach(foreach);
        outputs.forEach(foreach);
    }
    public Socket getSocketByVariableName(String name) {
        for (var s : inputs) {
            if (s.getVariable().getName().equals(name)) {
                return s;
            }
        }
        for (var s : outputs) {
            if (s.getVariable().getName().equals(name)) {
                return s;
            }
        }
        return null;
    }
    
    public long getId() {
        return id;
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
    public boolean isMandatory() {
        return mandatory;
    }
    
    @Override
    public String toString() {
        return "Module#"+id;
    }
    
    public static void setNextId(long next) {
        if (nextId < next) {
            nextId = next;
        }
    }
    public static long getNextId() {
        return nextId;
    }
    
}
