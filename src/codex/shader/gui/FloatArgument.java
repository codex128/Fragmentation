/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.shader.InputSocket;
import com.simsilica.lemur.TextField;

/**
 *
 * @author codex
 */
public class FloatArgument extends Argument {
    
    TextField field;
    
    public FloatArgument(InputSocket socket) {
        super(socket);
        initGui();
    }
    
    private void initGui() {
        field = new TextField(new NumberDocumentModel());
        addChild(field);
        reference = field.getDocumentModel().createReference();
    }
    
    @Override
    public void displayValue(String value) {
        field.setText(value);
    }
    @Override
    public String getDefaultValue() {
        return field.getText();
    }
    @Override
    public String getFallbackValue() {
        return "0.0";
    }
    
}
