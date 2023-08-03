/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import java.io.IOException;

/**
 *
 * @author codex
 */
public class SyntaxException extends IOException {
    
    public SyntaxException() {
        super();
    }
    public SyntaxException(String error) {
        super(error);
    }
    
}
