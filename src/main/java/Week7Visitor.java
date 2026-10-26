import generated.week7.week7BaseVisitor;
import generated.week7.week7Parser;
import impl.Scope;
import impl.SplcSemanticError;
import impl.symbol.FunctionSymbol;
import impl.symbol.VariableSymbol;
import impl.type.PrimitiveType;
import impl.type.Type;

import java.util.ArrayList;
import java.util.List;

public class Week7Visitor extends week7BaseVisitor<Void> {
    /**
     * 全局（根）作用域。
     */
    private final Scope globalScope = new Scope();

    /**
     * 遍历过程中当前所处的作用域。
     */
    private Scope currentScope = globalScope;

    /**
     * 收集到的语义错误。
     */
    private final List<SplcSemanticError> errors = new ArrayList<>();

    public Scope getGlobalScope() {
        return globalScope;
    }

    public List<SplcSemanticError> getErrors() {
        return errors;
    }

    @Override
    public Void visitProgram(week7Parser.ProgramContext ctx) {
        for (int i = 0; i < ctx.globalDef().size(); i++) {
            week7Parser.GlobalDefContext globalDefCtx = ctx.globalDef(i);
           // System.out.printf("Def %d: %s\n", i + 1, globalDefCtx.getText());
            visit(globalDefCtx);
        }
        return null;
    }

    @Override
    public Void visitGlobalDef(week7Parser.GlobalDefContext ctx) {
        if (ctx.LBRACE() != null && ctx.RBRACE() != null) {
            visitFuncDef(ctx);
        } else {
            visitVariableDec(ctx);
        }
        return null;
    }

    //Function Define:
    private void visitFuncDef(week7Parser.GlobalDefContext ctx) {
        String functionName = ctx.Identifier().getText();
        if (currentScope.lookupFunction(functionName) != null) {
            errors.add(SplcSemanticError.redeclaration(ctx.Identifier()));
            return;
        }

        List<VariableSymbol> parameters = collectParameters(ctx.funcArgs());
        
        currentScope.defineFunction(functionName, resolveType(ctx.specifier()), parameters);
        FunctionSymbol function = currentScope.lookupFunction(functionName);
        if (function == null) {
            return;
        }

        Scope enclosingScope = currentScope;
        currentScope = function;
        try {
            for (VariableSymbol parameter : parameters) {
                currentScope.defineVariable(parameter.getName(), parameter.getType());
            }
            for (week7Parser.StatementContext statement : ctx.statement()) {
                visit(statement);
            }
        } finally {
            currentScope = enclosingScope;
        }
    }

    private List<VariableSymbol> collectParameters(week7Parser.FuncArgsContext ctx) {
        List<VariableSymbol> parameters = new ArrayList<>();
        for (int i = 0; i < ctx.Identifier().size(); i++) {
            String parameterName = ctx.Identifier(i).getText();
            boolean duplicate = false;
            for (VariableSymbol parameter : parameters) {
                if (parameter.getName().equals(parameterName)) {
                    errors.add(SplcSemanticError.redeclaration(ctx.Identifier(i)));
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                parameters.add(new VariableSymbol(parameterName, resolveType(ctx.specifier(i))));
            }
        }
        return parameters;
    }


    //Variable Declare:
    private void visitVariableDec(week7Parser.GlobalDefContext ctx) {
        String variableName = ctx.Identifier().getText();
        if (currentScope.lookupCurrentVariable(variableName) != null) {
            errors.add(SplcSemanticError.redefinition(ctx.Identifier()));
            return;
        }

        currentScope.defineVariable(variableName, resolveType(ctx.specifier()));
    }

    @Override
    public Void visitCombineStmt(week7Parser.CombineStmtContext ctx) {
        Scope enclosingScope = currentScope;
        currentScope = new Scope(enclosingScope);
        try {
            for (week7Parser.StatementContext statement : ctx.statement()) {
                visit(statement);
            }
        } finally {
            currentScope = enclosingScope;
        }
        return null;
    }

    @Override
    public Void visitLocalVariableStmt(week7Parser.LocalVariableStmtContext ctx) {
        String variableName = ctx.Identifier().getText();
        if (currentScope.lookupCurrentVariable(variableName) != null) {
            errors.add(SplcSemanticError.redeclaration(ctx.Identifier()));
        } else {
            currentScope.defineVariable(variableName, resolveType(ctx.specifier()));
        }

        if (ctx.expression() != null) {
            visit(ctx.expression());
        }
        return null;
    }

    private Type resolveType(week7Parser.SpecifierContext ctx) {
        if (ctx.INT() != null) {
            return PrimitiveType.INT;
        } else if (ctx.CHAR() != null) {
            return PrimitiveType.CHAR;
        } else {
            return PrimitiveType.VOID;
        }
    }
}
