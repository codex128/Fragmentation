/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.shader.GlslVar;
import codex.shader.InputSocket;
import com.simsilica.lemur.TextField;

/**
 *
 * @author codex
 */
public class StateArgument extends StringArgument {
    
    private TextField field;
    private String[] states;
    
    public StateArgument(InputSocket socket) {
        super(socket);
        socket.getVariable().setType("int");
        initGui();
        initStates();
    }
    
    private void initGui() {
        field = new TextField("");
        addChild(field);
        reference = field.getDocumentModel().createReference();
    }
    private void initStates() {
        int seperator = socket.getVariable().getDefault().indexOf(':');
        if (seperator < 0) {
            throw new NullPointerException("Missing division between default argument and available states!");
        }
        states = socket.getVariable().getDefault().substring(seperator+1).split(",");
        displayValue(socket.getVariable().getDefault().substring(0, seperator));
        socket.getVariable().setDefault(getDefaultValue());
    }

    @Override
    public void displayValue(String value) {
        super.displayValue(states[Integer.parseInt(value)]);
    }
    @Override
    public String getDefaultValue() {
        var value = super.getDefaultValue();
        int i = 0;
        for (var v : states) {
            if (v.equals(value)) {
                System.out.println("get state: "+i);
                return ""+i;
            }
            i++;
        }
        return null;
    }
    @Override
    public String getFallbackValue() {
        return "0";
    }
    
    public static boolean isStateVar(GlslVar var) {
        return var.getDefault() != null
                && var.getDefault().contains(":");
    }
    
}
