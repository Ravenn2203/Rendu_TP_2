package org.example;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

public class TypeDeclarationVisitor extends ASTVisitor {
    List<TypeDeclaration> types = new ArrayList<>();

    public boolean visit(TypeDeclaration node) {
        types.add(node);
        return super.visit(node);
    }

    public List<TypeDeclaration> getTypes() {
        return types;
    }

}
