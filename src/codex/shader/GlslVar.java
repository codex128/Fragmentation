/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader;

import java.util.ArrayList;

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
    
    public static class Modifiers {
        
        /**
         * Indicates that the variable does not change.
         */
        public static final String CONSTANT = "constant";
        
        /**
         * Indicates that this variable is used only once, and should
         * not have a formal declaration.
         */
        public static final String SINGLE = "single";
        
        private static boolean isModifier(String string) {
            return string.equals(CONSTANT) || string.equals(SINGLE);
        }
        private static void apply(GlslVar var, String modifier) {
            switch (modifier) {
                case CONSTANT -> var.constant = true;
                case SINGLE    -> var.declare = false;

            }
        }
        
    }
    
    protected String function, name, type, def;
    private String compilerName;
    private GlslVar compileSource;
    private boolean constant = false, declare = true;
    
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
    public String renderUsages(String string) {
        if (compilerName == null) {
            throw new NullPointerException("Cannot compile \""+name+"\" because compiler-assigned name is null!");
        }
        var render = getRenderName();
        var compiled = new StringBuilder();
        var chunk = new StringBuilder();
        int index = 0;
        boolean build = false;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            chunk.append(c);
            boolean margin = !Character.isLetter(c) && !Character.isDigit(c) && c != '_';
            // if we've built a full variable, check if the current character is a margin,
            // if so, append the render name
            if (build && index == name.length()) {
                if (margin) {
                    compiled.append(render);
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
    public String renderDeclaration() {
        if (compilerName == null) {
            throw new NullPointerException("Cannot compile because compiler-assigned name is null!");
        }
        if (isConstant()) {
            // don't render a declaration
            return null;
        }
        return type+" "+compilerName+" = "+renderTypeCasting(compileSource != null ? compileSource.getCompilerName() : def)+";";
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
    public String getRenderName() {
        // The difference between this method and getCompilerName is that
        // this method is used to put the compiler name in the text,
        // while getCompilerName is used more by the compiler.
        if (isConstant()) {
            if (compileSource != null) return compileSource.getCompilerName();
            else return renderTypeCasting(def);
        }
        return compilerName;
    }
    public GlslVar getCompileSource() {
        return compileSource;
    }
    protected String renderTypeCasting(String value) {
        if (isConstant() && (type.equals("float") || type.equals("int"))) {
            return value;
        }
        else return type+"("+value+")";
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
    public boolean isConstant() {
        return constant;
    }
    public boolean isDeclared() {
        return declare;
    }
    
    public static GlslVar parse(String source) throws SyntaxException {
        String[] args = source.split(" ");
        String function, type = null, name = null, def = null;
        var modifiers = new ArrayList<String>();
        function = args[0];
        switch (function) {
            case INPUT -> {
                validate(args, 3);
                int i = 1;
                while (Modifiers.isModifier(args[i])) {
                    modifiers.add(args[i++]);
                }
                if (i+1 >= args.length) {
                    System.out.println("not enough info");
                    throw new NullPointerException("Missing variable data!");
                }
                type = args[i];
                name = args[i+1];
                if (args.length > i+2) {
                    def = args[i+2];
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
        if (type == null) var = new GlslVar();
        else var = switch (type) {
            case "generic" ->   new GenericVar();
            case "String" ->    new StringVar();
            default ->          new GlslVar();
        };
        var.function = function;
        var.name = name;
        if (type != null) var.type = type;
        if (def != null) var.def = def;
        for (var mod : modifiers) Modifiers.apply(var, mod);
        return var;
    }
    private static void validate(String[] args, int length) throws SyntaxException {
        if (args.length < length) {
            throw new SyntaxException("Expected minimum "+length+" argument"+(length != 1 ? "s" : "")
                    +", found "+args.length+" argument"+(args.length != 1 ? "s" : "")+"!");
        }
    }
    
}
