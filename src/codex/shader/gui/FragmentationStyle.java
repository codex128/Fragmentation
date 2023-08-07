/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.boost.ColorHSBA;
import codex.shader.Module;
import codex.shader.Socket;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.style.Styles;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class FragmentationStyle {
    
    private static final HashMap<String, ColorRGBA> CLRS = new HashMap<>();
    static {
        CLRS.put("float", ColorRGBA.Green);
        CLRS.put("vec4", ColorRGBA.Red);
    }
    public static ColorRGBA getTypeColor(String type) {
        var clr = CLRS.get(type);
        if (clr != null) return clr;
        return ColorRGBA.Gray;
    }
    
    public static final String STYLE = "fragmentation-style";    
    public static void initialize(Styles styles) {
        var common = styles.getSelector(STYLE);
        common.set("fontSize", 14);
        var button = styles.getSelector(Button.ELEMENT_ID, STYLE);
        button.set("insets", new Insets3f(1f, 3f, 1f, 3f));
        button.set("background", new QuadBackgroundComponent(new ColorHSBA(0f, 0f, .1f, 1f).toRGBA()));
        var module = styles.getSelector(Module.ELEMENT_ID, STYLE);
        //module.set("preferredSize", new Vector3f(170f, 170f, 0f));
        module.set("background", new QuadBackgroundComponent(new ColorHSBA(0f, 0f, .05f, 1f).toRGBA()));
        var header = styles.getSelector("header", STYLE);
        header.set("fontSize", 20);
        header.set("insets", new Insets3f(10f, 3f, 10f, 3f));
        header.set("textHAligment", HAlignment.Center);
        header.set("background", new QuadBackgroundComponent(new ColorHSBA(0f, 0f, .06f, 1f).toRGBA()));
        var hub = styles.getSelector(SocketHub.ELEMENT_ID, STYLE);
        hub.set("preferredSize", new Vector3f(20, 10, 0));
        hub.set("insets", new Insets3f(3f, 3f, 3f, 3f));
        hub.set("background", new QuadBackgroundComponent(ColorRGBA.Red));
        var sockets = styles.getSelector(Socket.ELEMENT_ID, STYLE);
        //sockets.set("preferredSize", new Vector3f(20f, 170f, 0f));
        var arguments = styles.getSelector(Argument.ELEMENT_ID, STYLE);
        arguments.set("preferredSize", new Vector3f(70f, 5f, 0f));
        arguments.set("insets", new Insets3f(1f, 3f, 1f, 3f));
    }
    
}
