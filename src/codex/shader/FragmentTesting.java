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
    
    public static void main(String[] args) {
        new FragmentTesting().start();
    }
    
    @Override
    public void simpleInitApp() {
        
        var cube = new Geometry("test-cube", new Box(1f, 1f, 1f));
        var mat = new Material(assetManager, "MatDefs/Tester.j3md");
        //mat.setColor("Vec4", ColorRGBA.Blue);
        cube.setMaterial(mat);
        rootNode.attachChild(cube);
        
    }
    
}
