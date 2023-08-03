/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

/**
 *
 * @author codex
 */
public class GLSL {
    
    private enum ParseState {
        Def, Init, Main;
    }
    
    public static final String NAME_ID = "$name";
    
    private String name;
    private final ArrayList<String> init = new ArrayList<>();
    private final ArrayList<String> main = new ArrayList<>();
    private final ArrayList<GlslVar> variables = new ArrayList<>();
    private ParseState state;
    
    public GLSL(InputStream stream) throws IOException, SyntaxException {
        read(stream);
    }
    
    private void read(InputStream stream) throws IOException, SyntaxException {
        var reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            parse(line);
        }
        if (name == null) {
            name = "Unnamed-GLSL";
        }
    }
    private void parse(String line) throws SyntaxException {
        String data = line.trim();
        if (data.startsWith("//")) return;
        switch (state) {
            case Def -> {
                if (data.startsWith("<init>")) {
                    state = ParseState.Init;
                }
                else if (data.startsWith("<main>")) {
                    state = ParseState.Main;
                }
                else if (data.startsWith(GlslVar.FUNCTION)) {
                    variables.add(GlslVar.parse(data));
                }
                else if (data.startsWith(NAME_ID+" ")) {
                    name = data.substring(NAME_ID.length()+1);
                    if (name.isBlank()) {
                        name = null;
                    }
                }
            }
            case Init -> {
                if (data.startsWith("</init>")) {
                    state = ParseState.Def;
                }
                else if (!line.isBlank()) {
                    init.add(line);
                }
            }
            case Main -> {
                if (data.startsWith("</main>")) {
                    state = ParseState.Def;
                }
                else if (!line.isBlank()) {
                    main.add(line);
                }
            }
        }
    }
    
    public String compileLine(String line) {
        for (var v : variables) {
            line = v.compileUsages(line);
        }
        return line;
    }
    public String compileInitLine(int index) {
        return compileLine(""+init.get(index));
    }
    public String compileMainLine(int index) {
        return compileLine(""+main.get(index));
    }
    
    public ArrayList<String> getInit() {
        return init;
    }
    public ArrayList<String> getMain() {
        return main;
    }
    public Collection<GlslVar> getVariables() {
        return variables;
    }
    public Stream<GlslVar> getGlobalVariables() {
        return variables.stream().filter(v -> !v.isLocal());
    }
    public Stream<GlslVar> getLocalVariables() {
        return variables.stream().filter(v -> v.isLocal());
    }
    public Stream<GlslVar> getInputVariables() {
        return variables.stream().filter(v -> v.isInput());
    }
    public Stream<GlslVar> getOutputVariables() {
        return variables.stream().filter(v -> v.isOutput());
    }
    
}
