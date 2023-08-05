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
        return string.replaceAll("{"+name+"}", def);
    }
    
}
