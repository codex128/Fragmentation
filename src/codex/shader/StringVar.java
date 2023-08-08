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
    public String renderDeclaration() {
        return null;
    }
    @Override
    public String renderUsages(String string) {
        return replaceUsages(string, "{"+name+"}", def);
    }
    
    public static String replaceUsages(String string, String replace, String replacement) {
        var compiled = new StringBuilder();
        var chunk = new StringBuilder();
        int index = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            // only build the chunk if the preceding character is a margin character
            if (c == replace.charAt(index)) {
                chunk.append(c);
                index++;
            }
            // dump chunk because it is invalid
            else {
                compiled.append(c);
                index = 0;
                if (!chunk.isEmpty()) {
                    chunk.delete(0, chunk.length());
                }
            }
            // if we've built a full variable, insert the replacement string
            if (index == replace.length()) {
                compiled.append(replacement);
                chunk.delete(0, chunk.length());
                index = 0;
            }
        }
        return compiled.toString();
    }
    
}
