import generated.week8.week8BaseVisitor;
import generated.week8.week8Parser;
import impl.Scope;
import impl.SplcSemanticError;
import impl.type.PrimitiveType;
import impl.type.Type;

import java.util.ArrayList;
import java.util.List;

public class SyntaxVisitor extends week8BaseVisitor<Void> {
    private Scope globalScope = new Scope(null);
    private Scope currentScope = globalScope;
    private final List<SplcSemanticError> errors = new ArrayList<>();

    public Scope getGlobalScope() {
        return globalScope;
    }

    public Scope getCurrentScope() {
        return currentScope;
    }

    public List<SplcSemanticError> getErrors() {
        return errors;
    }

    @Override
    public Void visitGlobalDef(week8Parser.GlobalDefContext ctx) {
        if (ctx.LBRACE() != null && ctx.RBRACE() != null) {
            visitFuncDef(ctx);
        } else {
            visitVariableDec(ctx);
        }
        return null;
    }

    //Variable Declare:
    private void visitVariableDec(week8Parser.GlobalDefContext ctx){

    }

    private void visitFuncDef(week8Parser.GlobalDefContext ctx) {

    }

    private Type resolveType(week8Parser.SpecifierContext ctx) {
        if (ctx.INT() != null) {
            return PrimitiveType.INT;
        } else if (ctx.CHAR() != null) {
            return PrimitiveType.CHAR;
        } else {
            return PrimitiveType.VOID;
        }
    }

}