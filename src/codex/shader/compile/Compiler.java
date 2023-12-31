/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.compile;

import codex.boost.Listenable;
import codex.shader.GLSL;
import codex.shader.StaticGlsl;
import codex.shader.Module;
import codex.shader.Program;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author codex
 */
public class Compiler implements Runnable, Listenable<CompileListener> {
    
    private static final String PREFIX = "gv_";
    private static final String[] letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    private static final String TAB = "    ";    
    private final static String[] STEPS = {"resources", "generics", "verify-generics", "inits", "statics", "main"};
    
    private final Program program;
    private final ConcurrentLinkedQueue<CompileListener> listeners = new ConcurrentLinkedQueue<>();
    private final LinkedList<Module> compileQueue = new LinkedList<>();
    private final ArrayList<String> compiledCode = new ArrayList<>();
    private final HashSet<String> resources = new HashSet<>();
    private Iterator<Module> moduleIterator;
    private Iterator<StaticGlsl> staticIterator;
    private Module currentModule;
    private GLSL staticSource;
    private StaticGlsl currentStatic;
    private int state = 0;
    private int substep = 0;
    private int nextNameIndex = 0;
    private int nameIteration = 0;
    private CompilingError error;
    
    public Compiler(Program program) {
        this.program = program;
    }
    
    @Override
    public void run() {
        initialize();
        while (true) {
            if (error != null || compile()) {
                break;
            }
        }
        cleanup();
        if (error == null) {
            System.out.println("\nCompile Successful!");
            notifyListeners(l -> l.compileFinished(this));
        }
        else {
            System.err.println("\nCompiler Error: "+error.getErrorMessage());
            notifyListeners(l -> l.compileError(error));
        }
    }    
    @Override
    public Collection<CompileListener> getListeners() {
        return listeners;
    }    
    public ArrayList<String> getCompiledCode() {
        return compiledCode;
    }
    
    // setup
    public void initialize() {
        queueModules();
        for (var m : compileQueue) {
            // generate names for variables that didn't recieve one during queueing (non-input variables)
            for (var v : m.getGlsl().getVariables()) {
                if (v.getCompilerName() != null) continue;
                v.setCompilerName(generateNextName());
            }
            // assign compile sources to static code
            var statics = GLSL.getStaticGlsl(m.getGlsl().getName());
            if (statics != null && statics.getCompileSource() == null) {
                statics.setCompileSource(m.getGlsl());
            }
        }
    }
    private void queueModules() {
        program.getOutputModule().setCompileLayer(0);
        int max = bump(program.getOutputModule(), 0);
        for (Module m : program.getModules()) {
            if (!m.getGlsl().isTerminal()) continue;
            m.setCompileLayer(0);
            max = bump(m, max);
        }
        // sort by compile layer
        var table = new ArrayList<LinkedList<Module>>();
        for (int i = 0; i <= max; i++) {
            table.add(new LinkedList<>());
        }
        // add the main output module first, so that it will compile last
        table.get(0).add(program.getOutputModule());
        // sort in other modules
        main: for (var m : program.getModules()) {
            if (m == program.getOutputModule()) continue;
            if (m.getCompileLayer() > max) {
                throw new IllegalStateException("Compile layer does not exist ("+m.getCompileLayer()+")!");
            }
            // if the compile layer wasn't changed, the module isn't used
            if (m.getCompileLayer() < 0) {
                continue;
            }
            table.get(m.getCompileLayer()).addLast(m);
        }
        // now that it's sorted, just pop them into the compile queue in order
        for (var l : table) {
            for (var m : l) {
                compileQueue.addFirst(m);
            }
            l.clear();
        }
        table.clear();
    }
    private int bump(Module module, int max) {
        // Updates each input module's compile layer so that it is
        // higher than this module's compile layer.
        // This method is recursive, so calling this once should
        // sort a whole tree of modules correctly.
        for (var s : module.getInputSockets()) {
            // generate a unique name for this variable
            if (s.getVariable().getCompilerName() == null) {
                s.getVariable().setCompilerName(generateNextName());
            }
            // if this has no connections, there is no need to do further calculations
            if (s.getNumConnections() == 0) {
                // parse the argument gui to the variable
                if (s.getArgument() != null && !s.getArgument().compile(s.getVariable())) {
                    // the gui argument was found to be invalid
                    error = new CompilingError("Illegal Argument!");
                }
                continue;
            }
            // set the compiler source variable
            if (s.getVariable().getCompileSource() == null) {
                s.getVariable().setCompileSource(s.getConnection().getOutputSocket().getVariable());
            }
            // check if an input module not higher than this module
            var m = s.getConnection().getOutputSocket().getModule();
            if (m.getCompileLayer() <= module.getCompileLayer()) {
                // bump up the input module so that it's higher than this module
                m.setCompileLayer(module.getCompileLayer()+1);
                // now that the input module is moved, we have to bump it's input modules, too
                int i = bump(m, m.getCompileLayer() > max ? m.getCompileLayer() : max);
                // save the maximum layer
                if (i > max) {
                    max = i;
                }
                // If the modules are connected in a loop (illegal), then the max layer should should take off into space.
                // Unfortunately, there is no good way to stop this before it happens.
                // Interdependence will result in a stack-overflow exception and crash the application.
            }
        }
        return max;
    }
    private String generateNextName() {
        if (nextNameIndex >= letters.length) {
            nextNameIndex = 0;
            nameIteration++;
        }
        return PREFIX+letters[nextNameIndex++]+(nameIteration > 0 ? nameIteration : "");
    }
    
    // compile
    public void update() {
        if (error != null) {
            notifyListeners(l -> l.compileError(error));
        }
        if (compile()) {
            notifyListeners(l -> l.compileFinished(this));
        }
    }
    private boolean compile() {
        if (runCompileStep(state)) {
            currentModule = null;
            substep = 0;
            switch (STEPS[state]) {
                case "statics" -> {
                    append("void main() {");
                }
                case "main" -> {
                    append("}");
                    return true;
                }
                default -> moduleIterator = null;
            }
            state++;
        }
        return false;
    }
    private boolean runCompileStep(int step) {
        return switch (STEPS[step]) {
            case "resources"        -> compileResources();
            case "generics"         -> compileGenerics();
            case "verify-generics"  -> verifyGenerics();
            case "inits"            -> compileInitCode();
            case "statics"          -> compileStaticCode();
            case "main"             -> compileMainCode();
            default                 -> true;
        };
    }
    private void append(String line) {
        //System.out.println(line);
        compiledCode.add(line);
    }
    private boolean compileResources() {
        if (fetchNextModule()) return true;
        if (!currentModule.getGlsl().getResources().isEmpty()) {
            var res = currentModule.getGlsl().getResources().get(substep).getResource();
            if (!resources.contains(res)) {
                resources.add(res);
                append(currentModule.getGlsl().compileResources(substep));
            }
            substep++;
        }
        if (substep >= currentModule.getGlsl().getResources().size()) {
            currentModule = null;
            substep = 0;
        }
        return false;
    }
    private boolean compileGenerics() {
        if (moduleIterator == null) {
            moduleIterator = compileQueue.iterator();
        }
        if (currentModule == null) {
            if (!moduleIterator.hasNext()) {
                moduleIterator = null;
                return true;
            }
            currentModule = moduleIterator.next();
            if (currentModule.getGlsl().getVariables().isEmpty()) {
                currentModule = null;
                return false;
            }
        }
        while (true) {
            boolean found = !currentModule.getGlsl().compileGenerics(substep++);
            if (substep >= currentModule.getGlsl().getVariables().size()) {
                currentModule = null;
                substep = 0;
                break;
            }
            if (found) {
                break;
            }
        }
        return false;
    }
    private boolean verifyGenerics() {
        for (var m : compileQueue) {
            for (var s : m.getInputSockets()) {
                if (s.getNumConnections() != 0 && !s.varTypeMatch(s.getConnection().getOutputSocket())) {
                    error = new CompilingError(s.getVariable().getCompileSource().getType()+" cannot be cast to "+s.getVariable().getType());
                    return false;
                }
            }
        }
        return true;
    }
    private boolean compileInitCode() {
        if (fetchNextModule()) return true;
        if (!currentModule.getGlsl().getInitCode().isEmpty()) {
            System.out.println("compile init line from "+currentModule.getGlsl().getName());
            append(currentModule.getGlsl().compileInitLine(substep++));
        }
        if (substep >= currentModule.getGlsl().getInitCode().size()) {
            currentModule = null;
            substep = 0;
        }
        return false;
    }
    private boolean compileStaticCode() {
        if (staticIterator == null) {
            staticIterator = GLSL.getStaticGlsl().iterator();
        }
        if (currentStatic == null) {
            if (!staticIterator.hasNext()) {
                return true;
            }
            currentStatic = staticIterator.next();
        }
        if (staticSource == null) {
            for (var m : compileQueue) {
                if (m.getGlsl().getName().equals(currentStatic.getId())) {
                    staticSource = m.getGlsl();
                    break;
                }
            }
            if (staticSource == null) {
                staticIterator.remove();
                currentStatic = null;
                return false;
            }
        }
        if (!currentStatic.getCode().isEmpty()) {
            append(currentStatic.compileLine(substep++, staticSource));
        }
        if (substep >= currentStatic.getCodeLength()) {
            currentStatic = null;
            staticSource = null;
            substep = 0;
        }
        return false;
    }
    private boolean compileMainCode() {
        if (fetchNextModule()) return true;
        if (substep < currentModule.getInputSockets().size()) {            
            // declare variables
            var line = currentModule.getInputSockets().get(substep++).getVariable().renderDeclaration();
            if (line != null) append(TAB+line);
        }
        else if (!currentModule.getGlsl().getMainCode().isEmpty()) {
            // append main code
            append(TAB+currentModule.getGlsl().compileMainLine((substep++)-currentModule.getInputSockets().size()));
        }
        if (substep >= currentModule.getInputSockets().size()+currentModule.getGlsl().getMainCode().size()) {
            currentModule = null;
            substep = 0;
        }
        return false;
    }
    private boolean fetchNextModule() {
        if (moduleIterator == null) {
            moduleIterator = compileQueue.iterator();
        }
        if (currentModule == null) {
            if (!moduleIterator.hasNext()) {
                moduleIterator = null;
                return true;
            }
            currentModule = moduleIterator.next();
        }
        return false;
    }
    
    // cleanup
    public void cleanup() {
        moduleIterator = null;
        staticIterator = null;
        compileQueue.clear();
        resources.clear();
        for (var m : program.getModules()) {
            m.setCompileLayer(-1);
            for (var v : m.getGlsl().getVariables()) {
                v.setCompilerName(null);
                v.setCompileSource(null);
            }
        }
        for (var s : GLSL.getStaticGlsl()) {
            s.setCompileSource(null);
        }
    }
    
}
