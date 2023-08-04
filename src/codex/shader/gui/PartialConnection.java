/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public class PartialConnection extends LineGeometry {
    
    SocketHub hub;
    Vector3f cursor = new Vector3f();

    public PartialConnection(SocketHub hub) {
        super("partial-connection");
        this.hub = hub;
    }
    
    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);
        setPoints(hub.getWorldTranslation(), cursor);
    }
    public void setCursorLocation(Vector3f cursor) {
        this.cursor.set(cursor);
    }
    
    public SocketHub getConnectedHub() {
        return hub;
    }
    
}
