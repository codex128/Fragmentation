/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

/**
 *
 * @author codex
 */
public class GlslVar {
    
    public static final String FUNCTION = "@";
    public static final String
            INPUT = FUNCTION+"input",
            LOCAL = FUNCTION+"local",
            STATIC = FUNCTION+"static",
            OUTPUT = FUNCTION+"output";
    
    protected String function, name, type, def;
    private String compilerName;
    private GlslVar compileSource;
    
    protected GlslVar() {}
    public GlslVar(String function, String name, String type, String def) {
        this.function = function;
        this.name = name;
        this.type = type;
        this.def = def;
    }
    
    public void setCompilerName(String name) {
        assert name == null || !name.isBlank();
        compilerName = name;
    }
    public void setCompileSource(GlslVar source) {
        compileSource = source;
    }
    public String compileUsages(String string) {
        if (compilerName == null) {
            throw new NullPointerException("Cannot compile \""+name+"\" because compiler-assigned name is null!");
        }
        var compiled = new StringBuilder();
        var chunk = new StringBuilder();
        int index = 0;
        boolean build = false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            chunk.append(c);
            boolean margin = !Character.isLetter(c) && !Character.isDigit(c) && c != '_';
            // if we've built a full variable, check if the current character is a margin,
            // if so, append the compiler name
            if (build && index == name.length()) {
                if (margin) {
                    compiled.append(compilerName);
                    compiled.append(c);
                }
                else {
                    compiled.append(chunk);
                }
                chunk.delete(0, chunk.length());
                index = 0;
            }
            // only build the chunk if the preceding character is a margin character
            else if (build && c == name.charAt(index)) {
                index++;
            }
            // dump chunk because it is invalid
            else if (!chunk.isEmpty()) {
                compiled.append(chunk);
                chunk.delete(0, chunk.length());
                index = 0;
            }
            // if not actively building, check if we can build
            if (chunk.isEmpty()) {
                build = margin;
            }
        }
        return compiled.toString();
    }
    public String compileDeclaration() {
        if (compilerName == null) {
            throw new NullPointerException("Cannot compile because compiler-assigned name is null!");
        }
        return type+" "+compilerName+" = "+type+"("+(compileSource != null ? compileSource.getCompilerName() : def)+");";
    }
    
    protected void setName(String name) {
        this.name = name;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setDefault(String def) {
        assert def != null;
        this.def = def;
    }
    
    public String getFunction() {
        return function;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getDefault() {
        return def;
    }
    public String getCompilerName() {
        return compilerName;
    }
    public GlslVar getCompileSource() {
        return compileSource;
    }
    
    public boolean isInput() {
        return function.equals(INPUT);
    }
    public boolean isLocal() {
        return function.equals(LOCAL);
    }
    public boolean isStatic() {
        return function.equals(STATIC);
    }
    public boolean isOutput() {
        return function.equals(OUTPUT);
    }
    public boolean isGeneric() {
        return !isLocal() && !isStatic() && type.startsWith("<") && type.endsWith(">");
    }
    
    public static GlslVar parse(String source) throws SyntaxException {
        String[] args = source.split(" ", 4);
        String function, type = null, name = null, def = null;
        function = args[0];
        switch (function) {
            case INPUT -> {
                validate(args, 3);
                type = args[1];
                name = args[2];
                if (args.length > 3) {
                    def = args[3];
                }
            }
            case LOCAL -> {
                validate(args, 2);
                name = args[1];
            }
            case STATIC -> {
                validate(args, 2);
                name = args[1];
            }
            case OUTPUT -> {
                validate(args, 3);
                type = args[1];
                name = args[2];
            }
            default -> throw new SyntaxException("Unknown identifier \""+function+"\"!");
        }
        if (name == null) {
            throw new NullPointerException("Variable name cannot be null!");
        }
        if (!Character.isLetter(name.charAt(0))) {
            throw new SyntaxException("Variable name cannot begin with '"+name.charAt(0)+"'!");
        }
        GlslVar var;
        if (null == type) {
            var = new GlslVar();
        }
        else var = switch (type) {
            case "generic" ->   new GenericVar();
            case "String" ->    new StringVar();
            default ->          new GlslVar();
        };
        var.function = function;
        var.name = name;
        if (type != null) var.type = type;
        if (def != null) var.def = def;
        return var;
    }
    private static void validate(String[] args, int length) throws SyntaxException {
        if (args.length < length) {
            throw new SyntaxException("Expected minimum "+length+" argument"+(length != 1 ? "s" : "")
                    +", found "+args.length+" argument"+(args.length != 1 ? "s" : "")+"!");
        }
    }
    
}
