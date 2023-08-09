/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import java.util.ArrayList;

/**
 *
 * @author codex
 */
public class StaticGlsl {
    
    private final String id;
    private final ArrayList<String> code = new ArrayList<>();
    private GLSL compileSource;
    
    public StaticGlsl(String id) {
        this.id = id;
    }
    
    public void append(String line) {
        code.add(line);
    }
    public void close() {
        this.compileSource = null;
    }
    
    public void setCompileSource(GLSL src) {
        compileSource = src;
    }
    public String compileLine(int index, GLSL source) {
        var line = code.get(index);
        for (var v : source.getVariables()) {
            if (!v.isStatic()) continue;
            line = v.renderUsages(line);
        }
        return line;
    }
    
    public String getId() {
        return id;
    }
    public ArrayList<String> getCode() {
        return code;
    }
    public int getCodeLength() {
        return code.size();
    }
    public GLSL getCompileSource() {
        return compileSource;
    }
    
}
