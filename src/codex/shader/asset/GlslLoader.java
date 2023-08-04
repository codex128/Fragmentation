/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.asset;

import codex.shader.GLSL;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.IOException;

/**
 *
 * @author codex
 */
public class GlslLoader implements AssetLoader {

    @Override
    public GLSL load(AssetInfo assetInfo) throws IOException {
        System.out.println("Loaded GLSL shader file from "+assetInfo.getKey().getName());
        return new GLSL(assetInfo);
    }
    
}
