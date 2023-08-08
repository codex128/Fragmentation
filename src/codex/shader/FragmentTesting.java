/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author codex
 */
public class FragmentTesting extends SimpleApplication {
    
    Material mat;
    float value = -1.2f;
    float speed = .5f;
    
    public static void main(String[] args) {
        new FragmentTesting().start();
    }
    
    @Override
    public void simpleInitApp() {
        
        var cube = new Geometry("test-cube", new Box(1f, 1f, 1f));
        mat = new Material(assetManager, "MatDefs/Tester.j3md");
        mat.setFloat("Dissolve", value);
        cube.setMaterial(mat);
        rootNode.attachChild(cube);
        
    }
    @Override
    public void simpleUpdate(float tpf) {
        value += tpf*speed;
        if (value > 1f) {
            value = 1f;
            speed = -speed;
        }
        else if (value < -1.2f) {
            value = -1.2f;
            speed = -speed;
        }
        mat.setFloat("Dissolve", value);
    }
    
}
