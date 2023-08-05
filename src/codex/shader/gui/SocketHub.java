/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.shader.Socket;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.style.ElementId;

/**
 *
 * @author codex
 */
public class SocketHub extends Container {
    
    public static final String ELEMENT_ID = "socketHub";
    
    private final Socket socket;
    private IconComponent icon;
    
    public SocketHub(Socket socket, ColorRGBA color) {
        super(new ElementId(ELEMENT_ID));
        this.socket = socket;
        setBackground(new QuadBackgroundComponent(color));
    }
    
    public Socket getSocket() {
        return socket;
    }
    public Vector3f getPortLocation() {
        return getWorldTranslation().add(getSize().divide(2));
    }
    
}
