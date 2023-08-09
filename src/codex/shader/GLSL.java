/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import codex.shader.asset.FileBrowser;
import codex.shader.asset.GlslKey;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
    
    public static final String NAME_ID = "$name", IMPORT_ID = "#import", OUTPUT_ID = "$output";
    public static final String NATIVE = "native", ADDON = "addon";
    private static final HashMap<String, StaticGlsl> statics = new HashMap<>();
    
    private String[] locator;
    private String name;
    private boolean output = false;
    private final ArrayList<Resource> resources = new ArrayList<>();
    private final ArrayList<String> init = new ArrayList<>();
    private final ArrayList<String> main = new ArrayList<>();
    private final ArrayList<GlslVar> variables = new ArrayList<>();
    private final ArrayList<GlslVar> globals = new ArrayList<>();
    private StaticGlsl staticCode;
    private ParseState state;
    
    public GLSL(AssetInfo info) throws IOException, SyntaxException {
        read(info.openStream());
    }
    public GLSL(File file) throws IOException, SyntaxException {
        if (!file.getName().endsWith(".sn")) {
            throw new IllegalArgumentException("Incorrect file type!");
        }
        read(new FileInputStream(file));
    }
    
    private void read(InputStream stream) throws IOException, SyntaxException {
        var reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        state = ParseState.Def;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) continue;
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
                if (data.startsWith("<static>")) {
                    if (staticCode == null) {
                        staticCode = addStaticIfAbsent(new StaticGlsl(name));
                        if (staticCode != null) {
                            System.out.println("create static code: "+staticCode.getId());
                        }
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
                    var v = GlslVar.parse(data);
                    if (v.isGlobal()) globals.add(v);
                    else variables.add(v);
                }
                else if (data.startsWith(NAME_ID+" ")) {
                    name = data.substring(NAME_ID.length()+1);
                    if (name.isBlank()) {
                        name = null;
                    }
                }
                else if (data.startsWith(IMPORT_ID+" ")) {
                    var args = data.substring(IMPORT_ID.length()+1).split(" ", 2);
                    var res = new Resource(args[0]);
                    if (args.length > 1) {
                        res.setHyperlink(args[1]);
                    }
                    resources.add(res);
                }
                else if (data.startsWith(OUTPUT_ID)) {
                    output = true;
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
                    staticCode.append(line);
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
    
    public void applyDefault(String[] def) {
        assert def.length == 2;
        if (def[0].isBlank()) return;
        var variable = variables.stream().filter(v -> v.getName().equals(def[0])).findAny().orElse(null);
        if (variable == null) return;
        variable.setDefault(def[1]);
    }
    public void setAssetLocator(String... locator) {
        assert locator.length == 2;
        this.locator = locator;
    }
    
    public String compileResources(int index) {
        var res = resources.get(index);
        return "#import \""+res.getResource()+"\"";
    }
    public boolean compileGenerics(int index) {
        var v = variables.get(index);
        if (v.isGeneric()) {
            for (var generic : variables) {
                if (v == generic) continue;
                if (generic instanceof GenericVar && ((GenericVar)generic).applyTypeToVar(v)) {
                    break;
                }
            }
            return true;
        }
        return false;
    }
    public String compileLine(String line) {
        // fetch delegate to use for static variables
        var delegate = getStaticGlsl(name);
        if (delegate != null && delegate.getCompileSource() == null) {
            throw new NullPointerException("Static code compile source cannot be null!");
        }
        for (var v : variables) {
            if (v.isStatic() && delegate != null && delegate.getCompileSource() != this) {
                var s = delegate.getCompileSource().getVariableNamed(v.getName());
                if (s == null) {
                    throw new NullPointerException("Static delegate missing information!");
                }
                line = s.renderUsages(line);
            }
            else {
                line = v.renderUsages(line);
            }
        }
        return line;
    }
    public String compileInitLine(int index) {
        return compileLine(""+init.get(index));
    }
    public String compileMainLine(int index) {
        return compileLine(""+main.get(index));
    }
    
    public String getName() {
        return name;
    }
    public String[] getAssetLocator() {
        if (locator == null) {
            throw new NullPointerException("GLSL asset locator is null!");
        }
        return locator;
    }
    public boolean isOutput() {
        return output;
    }
    public boolean isTerminal() {
        return getOutputVariables().noneMatch(v -> true);
    }
    public ArrayList<Resource> getResources() {
        return resources;
    }
    public ArrayList<String> getInitCode() {
        return init;
    }
    public ArrayList<String> getMainCode() {
        return main;
    }
    public ArrayList<GlslVar> getVariables() {
        return variables;
    }
    public ArrayList<GlslVar> getGlobalVariables() {
        return globals;
    }
    public Stream<GlslVar> getIOVariables() {
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
    public GlslVar getVariableNamed(String name) {
        return variables.stream().filter(v -> v.getName().equals(name)).findAny().orElse(null);
    }
    @Override
    public String toString() {
        return name;
    }
    
    public static GLSL fromLocator(AssetManager assetManager, String[] locator) {
        var glsl = switch (locator[0]) {
            case GLSL.NATIVE -> (GLSL)assetManager.loadAsset(new GlslKey(FileBrowser.path(ShaderNodeManager.NATIVES, locator[1])));
            case GLSL.ADDON  -> (GLSL)assetManager.loadAsset(new GlslKey(FileBrowser.path("addons", locator[1])));
            default -> throw new IllegalArgumentException("Unknown location type!");
        };
        glsl.setAssetLocator(locator);
        return glsl;
    }
    
    private static StaticGlsl addStaticIfAbsent(StaticGlsl sg) {
        return (statics.putIfAbsent(sg.getId(), sg) == null ? sg : null);
    }
    public static StaticGlsl getStaticGlsl(String key) {
        return statics.get(key);
    }
    public static boolean removeStaticGlsl(StaticGlsl sg) {
        return statics.remove(sg.getId(), sg);
    }
    public static Collection<StaticGlsl> getStaticGlsl() {
        return statics.values();
    }
    
}
