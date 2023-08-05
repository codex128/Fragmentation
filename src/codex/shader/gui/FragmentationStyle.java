/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.boost.ColorHSBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.style.Styles;

/**
 *
 * @author codex
 */
public class FragmentationStyle {
    
    public static final String STYLE = "fragmentation-style";
    
    public static void initialize(Styles styles) {
        var common = styles.getSelector(STYLE);
        common.set("fontSize", 14);
        var container = styles.getSelector(Container.ELEMENT_ID, STYLE);
        container.set("background", new QuadBackgroundComponent(new ColorHSBA(0f, 0f, .1f, 1f).toRGBA()));
        var header = styles.getSelector("header", STYLE);
        header.set("fontSize", 20);
        var hub = styles.getSelector(SocketHub.ELEMENT_ID, STYLE);
        hub.set("preferredSize", new Vector3f(40, 40, 0));
    }
    
}
