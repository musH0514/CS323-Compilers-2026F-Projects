import generated.week8.week8BaseListener;
import generated.week8.week8Parser;
import impl.Scope;
import impl.SplcSemanticError;
import impl.type.PrimitiveType;
import impl.type.Type;

import java.util.ArrayList;
import java.util.List;

public class SyntaxListener extends week8BaseListener {
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
    public void enterGlobalDef(week8Parser.GlobalDefContext ctx) {
        if (ctx.LBRACE() != null && ctx.RBRACE() != null) {
            visitFuncDef(ctx);
        } else {
            visitVariableDec(ctx);
        }
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