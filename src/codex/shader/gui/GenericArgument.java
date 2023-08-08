/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shader.gui;

import codex.shader.InputSocket;
import com.jme3.shader.VarType;
import com.simsilica.lemur.TextField;

/**
 *
 * @author codex
 */
public class GenericArgument extends Argument {
    
    private static final String[] GLSL_TYPES = {
        VarType.Boolean.getGlslType(),
        VarType.BufferObject.getGlslType(),
        VarType.Float.getGlslType(),
        VarType.Int.getGlslType(),
        VarType.IntArray.getGlslType(),
        VarType.Matrix3.getGlslType(),
        VarType.Matrix3Array.getGlslType(),
        VarType.Matrix4.getGlslType(),
        VarType.Matrix4Array.getGlslType(),
        VarType.Texture2D.getGlslType(),
        VarType.Texture3D.getGlslType(),
        VarType.TextureArray.getGlslType(),
        VarType.TextureBuffer.getGlslType(),
        VarType.TextureCubeMap.getGlslType(),
        VarType.Vector2.getGlslType(),
        VarType.Vector2Array.getGlslType(),
        VarType.Vector3.getGlslType(),
        VarType.Vector3Array.getGlslType(),
        VarType.Vector4.getGlslType(),
        VarType.Vector4Array.getGlslType()
    };
    
    private TextField field;
    
    public GenericArgument(InputSocket socket) {
        super(socket);
        initGui();
    }
    
    private void initGui() {
        field = new TextField("");
        addChild(field);
        reference = field.getDocumentModel().createReference();
    }
    
    @Override
    public void displayValue(String value) {
        if (!isGlslType(value)) {
            throw new IllegalArgumentException("Type \""+value+"\" is not a GLSL type!");
        }
        field.setText(value);
    }
    @Override
    public String getDefaultValue() {
        return field.getText();
    }
    @Override
    public String getFallbackValue() {
        return "float";
    }
    
    private static boolean isGlslType(String type) {
        for (var t : GLSL_TYPES) {
            if (t.equals(type)) return true;
        }
        return false;
    }
    
}
