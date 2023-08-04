/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.asset;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author codex
 */
public class Exporter {
    
    private ArrayList<String> lines = new ArrayList<>();
    
    public Exporter(ArrayList<String> lines) {
        this.lines = lines;
    }
    
    public void export(File file) throws IOException {
        if (file.exists()) file.delete();
        file.createNewFile();
        try (var writer = new FileWriter(file)) {
            for (String line : lines) {
                writer.write(line+"\n");
            }
        }
    }
    
}
