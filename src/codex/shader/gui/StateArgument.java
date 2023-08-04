/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.shader.InputSocket;

/**
 *
 * @author codex
 */
public class StateArgument extends StringArgument {
    
    private String[] index;
    
    public StateArgument(InputSocket socket) {
        super(socket);
        index = socket.getVariable().getDefaultProperties().split(",");
        for (int i = 0; i < index.length; i++) {
            index[i] = index[i].trim();
        }
    }

    @Override
    public void displayValue(String value) {
        super.displayValue(index[Integer.parseInt(value)]);
    }
    @Override
    public String getValue() {
        var value = super.getValue();
        int i = 0;
        for (var v : index) {
            if (v.equals(value)) return ""+i;
            i++;
        }
        throw new NullPointerException("An error occured while getting the default state value!");
    }
    
}
