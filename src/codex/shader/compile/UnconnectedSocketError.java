/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.compile;

import codex.shader.InputSocket;

/**
 *
 * @author codex
 */
public class UnconnectedSocketError extends CompilingError {
    
    private InputSocket socket;
    
    public UnconnectedSocketError(InputSocket socket) {
        this.socket = socket;
    }
    
    public InputSocket getSocket() {
        return socket;
    }
    @Override
    public String getErrorMessage() {
        return "Input socket \""+socket.getVariable().getName()+"\" must be connected to compile!";
    }
    
}
