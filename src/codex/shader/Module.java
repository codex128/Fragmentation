/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.event.MouseListener;
import com.simsilica.lemur.style.ElementId;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 *
 * @author codex
 */
public class Module extends Container implements MouseListener {
    
    private static long nextId = 0;
    
    private final long id;
    private final Program program;
    private final GLSL glsl;
    private final ArrayList<InputSocket> inputs = new ArrayList<>();
    private final ArrayList<OutputSocket> outputs = new ArrayList<>();
    
    public Module(Program program, GLSL glsl) {
        this(program, glsl, nextId++);
    }
    public Module(Program program, GLSL glsl, long id) {
        this.id = id;
        this.program = program;
        this.glsl = glsl;
        createSockets();
        initGui();
    }
    
    private void createSockets() {
        glsl.getInputVariables().forEach(v -> inputs.add(new InputSocket(this, v)));
        glsl.getOutputVariables().forEach(v -> outputs.add(new OutputSocket(this, v)));
    }
    private void initGui() {
        var layout = new BoxLayout(Axis.Y, FillMode.Even);
        setLayout(layout);
        addChild(new Label(glsl.getName(), new ElementId("header")));
        for (var s : outputs) {
            addChild(s);
        }
        for (var s : inputs) {
            addChild(s);
        }
    }

    @Override
    public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {}
    @Override
    public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {}
    @Override
    public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {}
    @Override
    public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {}
    
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
