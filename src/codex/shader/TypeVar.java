/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

/**
 *
 * @author codex
 */
public class TypeVar extends StringVar {
    
    protected TypeVar() {
        super();
    }
    public TypeVar(String function, String name, String type, String def) {
        super(function, name, type, def);
    }
    
    @Override
    public String compileUsages(String string) {
        return string.replaceAll("<"+name+">", def);
    }
    
}
