/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

/**
 *
 * @author codex
 */
public class StringVar extends GlslVar {
    
    protected StringVar() {
        super();
    }
    public StringVar(String function, String name, String type, String def) {
        super(function, name, type, def);
    }    
    
    @Override
    public String compileDeclaration() {
        return null;
    }
    @Override
    public String compileUsages(String string) {
        final var name = "{"+this.name+"}";
        var compiled = new StringBuilder();
        var chunk = new StringBuilder();
        int index = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == name.charAt(index)) {
                chunk.append(c);
                if (++index == name.length()) {
                    compiled.append(def);
                    chunk.delete(0, chunk.length());
                    index = 0;
                }
            }
            else {
                compiled.append(c);
                index = 0;
                if (!chunk.isEmpty()) {
                    chunk.delete(0, chunk.length());
                }
            }
        }
        return compiled.toString();
    }
    
}
