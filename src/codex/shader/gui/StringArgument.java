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
public class StringArgument extends Argument {
    
    private TextField field;
    
    public StringArgument(InputSocket socket) {
        super(socket);
        initGui();
    }
    
    private void initGui() {
        field = new TextField("");
        addChild(field);
    }

    @Override
    public void displayValue(String value) {
        field.setText(value);
    }
    @Override
    public String getDefaultValue() {
        return field.getText();
    }
    
}
