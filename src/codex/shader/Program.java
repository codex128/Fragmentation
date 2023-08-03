/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author codex
 */
public class Program {
    
    private ArrayList<Module> modules = new ArrayList<>();
    private Module output;
    
    public Collection<Module> getModules() {
        return modules;
    }
    public Module getOutputModule() {
        return output;
    }
    
}
