/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.asset;

import com.jme3.asset.AssetKey;
import com.jme3.asset.cache.AssetCache;

/**
 *
 * @author codex
 */
public class GlslKey extends AssetKey {

    public GlslKey(String name) {
        super(name);
    }
    public GlslKey() {
    }
    
    @Override
    public Class<? extends AssetCache> getCacheType() {
        return null;
    }
    
}
