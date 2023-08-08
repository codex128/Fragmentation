/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.shader.GlslVar;
import codex.shader.InputSocket;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.style.ElementId;

/**
 * Represents the gui to manipulate the default argument to a {@link GlslVar}.
 * 
 * @author codex
 */
public abstract class Argument extends Container {
    
    public static final String ELEMENT_ID = "argument";
    protected final InputSocket socket;
    protected VersionedReference reference;
    
    public Argument(InputSocket socket) {
        super(new ElementId(ELEMENT_ID));
        validate(socket.getVariable());
        this.socket = socket;
        initGui();
    }
    
    private void initGui() {
        setLayout(new BoxLayout());
    }
    
    public abstract void displayValue(String value);
    public abstract String getDefaultValue();
    public abstract String getFallbackValue();
    public boolean compile(GlslVar var) {
        var n = getDefaultValue();
        if (n != null) var.setDefault(n);
        return n != null;
    }    
    
    public VersionedReference getReference() {
        return reference;
    }
    
    public static Argument create(InputSocket socket) {
        var arg = make(socket);
//        if (arg != null) {
//            arg.displayValue(socket.getVariable().getDefault());
//        }
        return arg;
    }
    private static Argument make(InputSocket socket) {
        var type = socket.getVariable().getType();
        return switch (type) {
            case "float" -> new FloatArgument(socket);
            case "int" -> new IntegerArgument(socket);
            case "state" -> new StateArgument(socket);
            case "generic" -> new GenericArgument(socket);
            case "String" -> new StringArgument(socket);
            default -> null;
        };
    }
    private static void validate(GlslVar var) {
        if (!var.isInput() || var.getDefault() == null) {
            throw new IllegalArgumentException("GlslVar does not have a default!");
        }
    }
    
}
