/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.compile;

import codex.boost.Listenable;
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
    
    private static final String[] letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    private static final String TAB = "    ";
    
    private final Program program;
    private int state = 0;
    private final LinkedList<Module> compileQueue = new LinkedList<>();
    private final ArrayList<String> compiledCode = new ArrayList<>();
    private Iterator<Module> moduleIterator;
    private Module currentModule;
    private int substep = 0;
    private int nextNameIndex = 0;
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
        notifyListeners(l -> l.compileFinished(this));
        cleanupModules();
        if (error == null) {
            
        }
        else {
            System.err.println("Compiler Error: "+error.getErrorMessage());
        }
    }    
    @Override
    public Collection<CompileListener> getListeners() {
        return listeners;
    }    
    public ArrayList<String> getCompiledCode() {
        return compiledCode;
    }
    
    private void initialize() {
        queueModules();
        moduleIterator = compileQueue.iterator();
    }
    private boolean compile() {
        if (currentModule == null) {
            if (moduleIterator == null) {
                moduleIterator = compileQueue.iterator();
            }
            currentModule = moduleIterator.next();
            substep = 0;
        }
        if (runCompileStep(state, currentModule)) {
            currentModule = null;
            if (!moduleIterator.hasNext()) {
                state++;
                if (state == 1) {
                    append("void main() {");
                    state++;
                }
                else if (state == 3) {
                    append("}");
                    state++;
                }                
                if (state > 3) {
                    return true;
                }
                else {
                    moduleIterator = null;
                }
            }
        }
        return false;
    }
    
    // setup
    private void queueModules() {
        compileQueue.clear();
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
                    if (!socket.getVariable().getType().equals(socket.getVariable().getCompilerSource().getType())) {
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
            throw new IndexOutOfBoundsException("Compiler ran out of unique variable names!");
        }
        return letters[nextNameIndex++];
    }
    
    // compile
    private boolean runCompileStep(int step, Module m) {
        return switch (step) {
            case 0 -> compileInitCode(m);
            case 2 -> compileMainCode(m);
            default -> true;
        };
    }
    private void append(String line) {
        compiledCode.add(line);
    }
    private boolean compileInitCode(Module m) {
        System.out.println("compile init code...");
        if (m.getGlsl().getInit().isEmpty()) return true;
        append(m.getGlsl().compileInitLine(substep++));
        return substep >= m.getGlsl().getInit().size();
    }
    private boolean compileMainCode(Module m) {
        System.out.println("compile main code...");
        if (substep >= m.getInputSockets().size()) {
            // append main code
            append(TAB+m.getGlsl().compileMainLine((substep++)-m.getInputSockets().size()));
        }
        else {
            // declare variables
            var line = m.getInputSockets().get(substep++).getVariable().compileDeclaration();
            if (line != null) append(TAB+line);
        }
        return substep >= m.getInputSockets().size()+m.getGlsl().getMain().size();
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
