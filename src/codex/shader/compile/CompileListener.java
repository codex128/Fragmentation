/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.shader.compile;

/**
 *
 * @author codex
 */
public interface CompileListener {
    
    public void compileError(CompilingError error);
    public void compileFinished(Compiler compiler);
    
}
