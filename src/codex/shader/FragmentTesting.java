/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;
import jme3utilities.sky.SkyControl;
import jme3utilities.sky.StarsOption;

/**
 *
 * @author codex
 */
public class FragmentTesting extends SimpleApplication {
    
    // https://hub.jmonkeyengine.org/t/question-about-shader-editor/45498/19?u=codex
    
    Material mat;
    float value = -1.2f;
    float speed = .5f;
    DirectionalLight light;
    
    public static void main(String[] args) {
        new FragmentTesting().start();
    }
    
    @Override
    public void simpleInitApp() {
        
        flyCam.setMoveSpeed(5f);
        
        var cube = new Geometry("test-cube", new Box(1f, 1f, 1f));
        mat = new Material(assetManager, "MatDefs/Tester.j3md");
        mat.setFloat("Dissolve", value);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTransparent(true);
        cube.setMaterial(mat);
        cube.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(cube);
        
        light = new DirectionalLight(new Vector3f(0f, 0f, 1f), ColorRGBA.Gray);
        rootNode.addLight(light);        
        var pl = new PointLight(new Vector3f(-2f, -2f, -2f), ColorRGBA.White);
        pl.setRadius(100f);
        rootNode.addLight(pl);
        
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);        
        SkyControl skyControl = new SkyControl(assetManager, cam, .5f, StarsOption.TopDome, true);
        rootNode.addControl((Control)skyControl);
        skyControl.setCloudiness(0.8f);
        skyControl.setCloudsYOffset(0.4f);
        skyControl.setTopVerticalAngle(1.78f);
        skyControl.getSunAndStars().setHour(10);
        //Updater updater = skyControl.getUpdater();
        //updater.setAmbientLight(gi);
        //updater.setMainLight(sun);
        //updater.addShadowRenderer(dlsr);
        skyControl.setEnabled(true);
        
    }
    @Override
    public void simpleUpdate(float tpf) {
        light.setDirection(cam.getDirection());
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
