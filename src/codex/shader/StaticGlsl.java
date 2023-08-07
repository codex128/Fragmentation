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
    private GLSL source;
    
    public StaticGlsl(GLSL source) {
        this.id = source.getName();
        this.source = source;
    }
    
    public boolean append(GLSL source, String line) {
        if (this.source == null || this.source != source) return false;
        code.add(line);
        return true;
    }
    public void close() {
        this.source = null;
    }
    
    public String compileLine(int index, GLSL source) {
        var line = code.get(index);
        for (var v : source.getVariables()) {
            if (!v.isLocal()) continue;
            line = v.compileUsages(line);
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
    
}
