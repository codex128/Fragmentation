/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import com.jme3.asset.AssetInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 *
 * @author codex
 */
public class GLSL {
    
    private enum ParseState {
        Def, Static, Init, Main;
    }
    
    public static final String NAME_ID = "$name";
    private static final HashMap<String, GlslStatic> statics = new HashMap<>();
    
    private final AssetInfo info;
    private String name;
    private final ArrayList<String> init = new ArrayList<>();
    private final ArrayList<String> main = new ArrayList<>();
    private final ArrayList<GlslVar> variables = new ArrayList<>();
    private GlslStatic staticCode;
    private ParseState state;
    
    public GLSL(AssetInfo info) throws IOException, SyntaxException {
        this.info = info;
        read(this.info.openStream());
    }
    
    private void read(InputStream stream) throws IOException, SyntaxException {
        var reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        state = ParseState.Def;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) continue;
            parse(line);
        }
        if (main.isEmpty()) {
            throw new NullPointerException("Missing main code!");
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
                if (data.startsWith("<static>")) {
                    if (staticCode == null) {
                        staticCode = addStaticIfAbsent(new GlslStatic(this));
                    }
                    state = ParseState.Static;
                }
                else if (data.startsWith("<init>")) {
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
            case Static -> {
                if (data.startsWith("</static>")) {
                    state = ParseState.Def;
                    if (staticCode != null) {
                        staticCode.close();
                        staticCode = null;
                    }
                }
                else if (staticCode != null) {
                    staticCode.append(this, line);
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
    
    public boolean compileGenerics(int index) {
        var v = variables.get(index);
        if (v.isGeneric()) {
            for (var type : variables) {
                if (v == type) continue;
            }
            return true;
        }
        return false;
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
    
    public AssetInfo getAssetInfo() {
        return info;
    }
    public String getName() {
        return name;
    }
    public String getAssetName() {
        return info.getKey().getName();
    }
    public ArrayList<String> getInitCode() {
        return init;
    }
    public ArrayList<String> getMainCode() {
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
    
    private static GlslStatic addStaticIfAbsent(GlslStatic gs) {
        return (statics.putIfAbsent(gs.getId(), gs) == null ? gs : null);
    }
    public static GlslStatic getStaticGlsl(String key) {
        return statics.get(key);
    }
    public static boolean removeStaticGlsl(GlslStatic gs) {
        return statics.remove(gs.getId(), gs);
    }
    public static Collection<GlslStatic> getStaticGlsl() {
        return statics.values();
    }
    
}
