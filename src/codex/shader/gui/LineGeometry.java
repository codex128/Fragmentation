/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

/**
 *
 * @author codex
 */
public class LineGeometry extends Geometry {
    
    private Vector3f pointB = new Vector3f();
    
    public LineGeometry(String name) {
        super(name, new Line(Vector3f.ZERO, Vector3f.UNIT_XYZ));
    }
    public LineGeometry(String name, Vector3f a, Vector3f b) {
        this(name);
        setPoints(a, b);
    }
    
    public final void setPoints(Vector3f a, Vector3f b) {
        setLocalTranslation(a);
        setLocalScale(pointB.set(b).subtractLocal(a));
    }
    public Vector3f getPointA() {
        return getLocalTranslation();
    }
    public Vector3f getPointB() {
        return getLocalTranslation().add(pointB);
    }
    
}
