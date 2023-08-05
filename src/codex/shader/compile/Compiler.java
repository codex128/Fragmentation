/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.compile;

import codex.boost.Listenable;
import codex.shader.GLSL;
import codex.shader.GlslStatic;
import codex.shader.Module;
import codex.shader.Program;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class Compiler implements Runnable, Listenable<CompileListener> {
    
    private static final String PREFIX = "fav_";
    private static final String[] letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    private static final String TAB = "    ";
    
    private final static int
            GENERICS = 0,
            VERIFY_GEN = GENERICS+1,
            STATICS = VERIFY_GEN+1,
            INITS = STATICS+1,
            MAIN = INITS+1;
    
    private final Program program;
    private int state = 0;
    private final LinkedList<Module> compileQueue = new LinkedList<>();
    private final ArrayList<String> compiledCode = new ArrayList<>();
    private Iterator<Module> moduleIterator;
    private Iterator<GlslStatic> staticIterator;
    private Module currentModule;
    private GLSL staticSource;
    private GlslStatic currentStatic;
    private int substep = 0;
    private int nextNameIndex = 0;
    private int nameIteration = 0;
    private CompilingError error;
    private LinkedList<CompileListener> listeners = new LinkedList<>();
    
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
        cleanupModules();
        if (error == null) {
            notifyListeners(l -> l.compileFinished(this));            
        }
        else {
            System.err.println("Compiler Error: "+error.getErrorMessage());
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
    private void initialize() {
        queueModules();
        //moduleIterator = compileQueue.iterator();
        //staticIterator = GLSL.getStaticGlsl().iterator();
    }
    private void queueModules() {
        var stack = new LinkedList<Module>();
        // do the output module first, which is guaranteed to have no modules depending on it
        stack.add(program.getOutputModule());
        while (!stack.isEmpty()) {
            var current = stack.getLast();
            var independent = true;
            // check if the module depends on an unqueued module
            for (var socket : current.getInputSockets()) {
                // generate a unique name for this input variable
                if (socket.getVariable().getCompilerName() == null) {
                    socket.getVariable().setCompilerName(generateNextName());
                }
                // check if this input socket is connected to anything
                if (socket.getConnection() == null) {
                    // if there is an argument, compile the argument
                    if (socket.getArgument() != null) {
                        socket.getArgument().compile(socket.getVariable());
                    }
                    else if (socket.getVariable().getDefault() == null) {
                        // compiling must be stopped because a required socket was not connected to anything
                        error = new CompilingError("Socket \""+socket.getName()+"\" must be connected!");
                        return;
                    }
                    continue;
                }
                // since the input variable is connected to an output variable, set the output as the input's source
                if (socket.getVariable().getCompilerSource() == null) {
                    socket.getVariable().setCompilerSource(socket.getConnection().getOutputSocket().getVariable());
                    if (!socket.varTypeCompatible(socket.getConnection().getOutputSocket())) {
                        // cannot continue compiling because two sockets are connected illegally
                        error = new CompilingError(socket.getVariable().getType()+" cannot be cast to "+socket.getVariable().getCompilerSource().getType());
                        return;
                    }
                }
                // we can check if it's unqueued by the source variable compiler name
                if (socket.getVariable().getCompilerSource().getCompilerName() == null) {
                    independent = false;
                    // append the dependency module to the stack
                    stack.addLast(socket.getConnection().getOutputSocket().getModule());
                }
            }
            if (independent) {
                // all output variables will need a unique name
                // this is important to do now, because we use the compiler name state for queueing
                current.getGlsl().getOutputVariables().forEach(v -> {
                    v.setCompilerName(generateNextName());
                });
                // append to the compile queue
                compileQueue.addLast(current);
                // since a module is only independent if it doesn't add anything, we can use removeLast
                stack.removeLast();
            }
        }
    }
    private String generateNextName() {
        if (nextNameIndex >= letters.length) {
            nextNameIndex = 0;
            nameIteration++;
        }
        return PREFIX+letters[nextNameIndex++]+(nameIteration > 0 ? nameIteration : "");
    }
    
    // compile
    private boolean compile() {
        if (runCompileStep(state)) {
            currentModule = null;
            substep = 0;
            switch (state) {
                case INITS -> {
                    append("void main() {");
                }
                case MAIN -> {
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
        return switch (step) {
            case GENERICS   -> compileGenerics();
            case VERIFY_GEN -> verifyGenerics();
            case STATICS    -> compileStaticCode();
            case INITS      -> compileInitCode();
            case MAIN       -> compileMainCode();
            default         -> true;
        };
    }
    private void append(String line) {
        System.out.println("append ("+line.length()+"): "+line);
        compiledCode.add(line);
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
                if (s.getConnection() != null && !s.varTypeMatch(s.getConnection().getOutputSocket())) {
                    error = new CompilingError(s.getVariable().getCompilerSource().getType()+" cannot be cast to "+s.getVariable().getType());
                    return false;
                }
            }
        }
        return true;
    }
    private boolean compileStaticCode() {
        if (staticIterator == null) {
            staticIterator = GLSL.getStaticGlsl().iterator();
        }
        if (currentStatic == null) {
            if (!staticIterator.hasNext()) return true;
            currentStatic = staticIterator.next();
        }
        if (staticSource == null) {
            for (var m : compileQueue) {
                if (m.getGlsl().getAssetName().equals(currentStatic.getId())) {
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
    private boolean compileInitCode() {
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
        if (!currentModule.getGlsl().getInitCode().isEmpty()) {
            append(currentModule.getGlsl().compileInitLine(substep++));
        }
        if (substep >= currentModule.getGlsl().getInitCode().size()) {
            currentModule = null;
            substep = 0;
        }
        return false;
    }
    private boolean compileMainCode() {
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
        if (substep < currentModule.getInputSockets().size()) {            
            // declare variables
            var line = currentModule.getInputSockets().get(substep++).getVariable().compileDeclaration();
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
    
    // cleanup
    private void cleanupModules() {
        for (var m : program.getModules()) {
            for (var v : m.getGlsl().getVariables()) {
                v.setCompilerName(null);
                v.setCompilerSource(null);
            }
        }
    }
    
}
