/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.compile;

/**
 *
 * @author codex
 */
public class CompilingError {
    
    private String msg;
    
    public CompilingError(String msg) {
        this.msg = msg;
    }
    
    public String getErrorMessage() {
        return msg;
    }
    
}
