/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.shader.gui.LineGeometry;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author codex
 */
public class Connection extends AbstractControl {
    
    private Socket out, in;
    private LineGeometry line;
    
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
    
    public LineGeometry createLineGeometry(Material mat) {
        LineGeometry g = new LineGeometry("connection-line");
        g.setMaterial(mat);
        g.addControl(this);
        return g;
    }
    public LineGeometry getLineGeometry() {
        return line;
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
        if (!s1.getVariable().getType().equals(s2.getVariable().getType())) {
            throw new IllegalStateException("Both socket's variable types must match!");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        line.setPoints(out.getConnectionLocation(), in.getConnectionLocation());
    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    @Override
    public void setSpatial(Spatial spat) {
        super.setSpatial(spat);
        if (spatial != null) {
            if (!(spatial instanceof LineGeometry)) {
                throw new IllegalArgumentException("Requires LineGeometry!");
            }
            line = (LineGeometry)spatial;
        }
        else {
            line = null;
        }
    }
    
    public void terminate() {
        out.removeConnection(this);
        in.removeConnection(this);
        out = in = null;
        spatial.removeFromParent();
        spatial.removeControl(this);
    }
    
}
