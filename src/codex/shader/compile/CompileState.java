/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.compile;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;

/**
 *
 * @author codex
 */
public class CompileState extends BaseAppState implements CompileListener {
    
    private Compiler compiler;
    
    @Override
    protected void initialize(Application app) {}
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (compiler != null) {
            compiler.update();
        }
    }    
    @Override
    public void compileError(CompilingError error) {
        compiler.removeListener(this);
        compiler.cleanup();
        compiler = null;
    }
    @Override
    public void compileFinished(Compiler compiler) {
        this.compiler.removeListener(this);
        this.compiler.cleanup();
        this.compiler = null;
    }
    
    public void compile(Compiler compiler) {
        this.compiler = compiler;
        this.compiler.addListener(this);
        this.compiler.initialize();
    }
    
}
