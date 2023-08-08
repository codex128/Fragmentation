/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

/**
 *
 * @author codex
 */
public class GenericVar extends StringVar {
    
    protected GenericVar() {
        super();
    }
    public GenericVar(String function, String name, String type, String def) {
        super(function, name, type, def);
        if (def == null) {
            // if not default is specified, the default reverts to float
            setDefault("float");
        }
    }
    
    @Override
    public String renderUsages(String string) {
        return StringVar.replaceUsages(string, "<"+name+">", def);
    }
    @Override
    public boolean isGeneric() {
        // GenericVar cannot be of a generic type
        return false;
    }
    
    public boolean applyTypeToVar(GlslVar var) {
        if (var.getType().equals("<"+name+">")) {
            var.setType(def);
            return true;
        }
        return false;
    }
    
}
