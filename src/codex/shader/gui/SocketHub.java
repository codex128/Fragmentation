/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.shader.Socket;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.IconComponent;

/**
 *
 * @author codex
 */
public class SocketHub extends Label {
    
    private final Socket socket;
    private IconComponent icon;
    
    public SocketHub(Socket socket, String iconPath) {
        super("");
        this.socket = socket;
        icon = new IconComponent(iconPath);
        setIcon(icon);
    }
    
    public Socket getSocket() {
        return socket;
    }
    @Override
    public IconComponent getIcon() {
        return icon;
    }
    
}
