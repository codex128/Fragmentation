/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

/**
 *
 * @author codex
 */
public class Connection {
    
    private final Socket out, in;
    
    public Connection(Socket s1, Socket s2) {
        validate(s1, s2);
        if (s1.getType() == Socket.IO.Input) {
            in = s1;
            out = s2;
        }
        else {
            in = s2;
            out = s1;
        }
    }
    
    public Socket getOutputSocket() {
        return out;
    }
    public Socket getInputSocket() {
        return in;
    }
    
    private static void validate(Socket s1, Socket s2) {
        if (s1.getType() == s2.getType()) {
            throw new IllegalStateException("Must have opposite I/O types!");
        }
        if (!s1.getVariable().getType().equals(s2.getVariable().getType())) {
            throw new IllegalStateException("Both socket's variable types must match!");
        }
    }
    
}
