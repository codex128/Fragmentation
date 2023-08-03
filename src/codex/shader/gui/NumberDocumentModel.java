/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import com.google.common.base.Function;
import com.simsilica.lemur.text.DefaultDocumentModel;
import com.simsilica.lemur.text.DocumentModel;
import com.simsilica.lemur.text.DocumentModelFilter;

/**
 *
 * @author codex
 */
public class NumberDocumentModel extends DocumentModelFilter {
    
    private static final Function<Character, Character> inputFilter = c -> {
        if (Character.isDigit(c) || c == '.') return c;
        else return null;
    };
    private static final Function<String, String> outputFilter = s -> s;
    
    public NumberDocumentModel() {
        this(new DefaultDocumentModel());
    }
    public NumberDocumentModel(DocumentModel model) {
        super(model, inputFilter, outputFilter);
    }
    
}
