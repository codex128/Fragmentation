/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.shader.gui.LineGeometry;
import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public class Connection extends LineGeometry {
    
    private Socket out, in;
    
    public Connection(Socket s1, Socket s2) {
        super("connection-geometry");
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
    public Socket getOppositeSocket(Socket socket) {
        if (socket == out) return in;
        else if (socket == in) return out;
        else return null;
    }
    
    private static void validate(Socket s1, Socket s2) {
        if (s1.getType() == s2.getType()) {
            throw new IllegalStateException("Must have opposite I/O types!");
        }
        if (!s1.varTypeCompatible(s2)) {
            throw new IllegalStateException("Both socket's variable types must match!");
        }
    }

    @Override
    public void updateLogicalState(float tpf) {
        //super.updateLogicalState(tpf);
        Vector3f parentLocation = parent.getWorldTranslation();
        setPoints(out.getConnectionLocation().subtract(parentLocation), in.getConnectionLocation().subtract(parentLocation));
    }
    
    public void terminate() {
        out.removeConnection(this);
        in.removeConnection(this);
        out = in = null;
        removeFromParent();
    }
    
}
