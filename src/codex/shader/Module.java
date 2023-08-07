/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.jme3.input.MouseInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.event.MouseListener;
import com.simsilica.lemur.style.ElementId;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 *
 * @author codex
 */
public class Module extends Container implements MouseListener {
    
    public static final String ELEMENT_ID = "module";
    private static long nextId = 0;
    
    private final long id;
    private final Program program;
    private final GLSL glsl;
    private final ArrayList<InputSocket> inputs = new ArrayList<>();
    private final ArrayList<OutputSocket> outputs = new ArrayList<>();
    private Vector3f drag;
    
    public Module(Program program, GLSL glsl) {
        this(program, glsl, nextId++);
    }
    public Module(Program program, GLSL glsl, long id) {
        super(new ElementId(ELEMENT_ID));
        this.id = id;
        this.program = program;
        this.glsl = glsl;
        createSockets();
        initGui();
    }
    
    private void createSockets() {
        glsl.getInputVariables().forEach(v -> {
            var in = InputSocket.create(this, v);
            in.initGui();
            inputs.add(in);
        });
        glsl.getOutputVariables().forEach(v -> {
            var out = new OutputSocket(this, v);
            out.initGui();
            outputs.add(out);
        });
    }
    private void initGui() {
        addControl(new MouseEventControl(this));
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
    public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
        if (target == this && event.isPressed() && event.getButtonIndex() == MouseInput.BUTTON_LEFT) {
            event.setConsumed();
            program.setSelected(this);
        }
        else {
            drag = null;
        }
    }
    @Override
    public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {}
    @Override
    public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {}
    @Override
    public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {
        if (capture == this) {
            if (drag == null) {
                drag = new Vector3f(event.getX(), event.getY(), 0f);
            }
            else {
                var current = new Vector3f(event.getX(), event.getY(), 0f);
                capture.move(current.subtract(drag).multLocal(1f));
                drag.set(current);
                event.setConsumed();
            }
        }
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
