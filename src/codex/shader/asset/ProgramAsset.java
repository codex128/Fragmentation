/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.asset;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.IOException;

/**
 *
 * @author codex
 */
public class ProgramAsset implements AssetLoader {
    
    private final AssetInfo info;
    
    public ProgramAsset() {
        info = null;
    }
    private ProgramAsset(AssetInfo info) {
        this.info = info;
    }
    
    @Override
    public ProgramAsset load(AssetInfo assetInfo) throws IOException {
        return new ProgramAsset(assetInfo);
    }
    public AssetInfo getInfo() {
        return info;
    }
    
}
