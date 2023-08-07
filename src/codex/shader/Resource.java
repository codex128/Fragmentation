/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

/**
 *
 * @author codex
 */
public class Resource {
    
    private String resource, href;
    
    public Resource(String resource) {
        this.resource = resource;
    }

    public void setHyperlink(String href) {
        this.href = href;
    }
    
    public String getResource() {
        return resource;
    }
    public String getHyperlink() {
        return href;
    }    
    
}
