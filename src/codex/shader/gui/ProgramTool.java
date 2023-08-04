/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.shader.Program;
import com.jme3.input.event.KeyInputEvent;
import com.simsilica.lemur.event.KeyListener;

/**
 * 
 * 
 * @author codex
 */
public abstract class ProgramTool implements KeyListener {
    
    protected final Program program;
    
    public ProgramTool(Program program) {
        this.program = program;
    }
    
    public Program getProgram() {
        return program;
    }
    
}
