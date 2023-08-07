/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.input;

import codex.shader.Program;
import codex.shader.Socket;
import codex.shader.gui.LineGeometry;
import codex.shader.gui.SocketHub;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.event.MouseListener;

/**
 *
 * @author codex
 */
public class SocketConnectorInterface extends LineGeometry implements MouseListener {
    
    private final Program program;
    private SocketHub hub;
    private Vector3f cursor = new Vector3f();

    public SocketConnectorInterface(Program program) {
        super("socket-connector-interface-geometry");
        this.program = program;
    }
    
    @Override
    public void updateLogicalState(float tpf) {
        //super.updateLogicalState(tpf);
        if (hub == null) return;
        Vector3f parent = getParent().getWorldTranslation();
        setPoints(hub.getSocket().getConnectionLocation().subtractLocal(parent), cursor.subtract(parent));
    }
    @Override
    public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
        if (event.isPressed() && target instanceof SocketHub) {
            var sh = (SocketHub)target;
            if (sh.getSocket().getType() == Socket.IO.Input && sh.getSocket().getNumConnections() > 0) {
                var c = sh.getSocket().getConnectionList().get(0);
                hub = c.getOutputSocket().getHub();
                c.terminate();
            }
            else {
                hub = sh;
            }
            program.getGuiScene().attachChild(this);
            event.setConsumed();
        }
        else if (event.isReleased() && hub != null && target != hub && target instanceof SocketHub) {
            var sh = (SocketHub)target;
            program.connect(hub.getSocket(), sh.getSocket());
            terminate();
            event.setConsumed();
        }
    }
    @Override
    public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {}
    @Override
    public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {}
    @Override
    public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {}

    public void setCursorLocation(Vector3f vec) {
        cursor.set(vec);
    }
    public void terminate() {
        if (hub == null) return;
        hub = null;
        removeFromParent();
    }
    
}
