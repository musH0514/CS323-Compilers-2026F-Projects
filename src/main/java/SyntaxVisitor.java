import generated.week8.week8BaseVisitor;
import generated.week8.week8Parser;
import impl.Scope;
import impl.SplcSemanticError;
import impl.symbol.FunctionSymbol;
import impl.symbol.VariableSymbol;
import impl.type.PrimitiveType;
import impl.type.Type;
import impl.type.ArrayType;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

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

    private void visitVariableDec(week8Parser.GlobalDefContext ctx){
        Type baseType = resolveType(ctx.specifier());
        week8Parser.VarDecContext varDec = ctx.varDec();
        TerminalNode identifierNode = getVarIdentifier(varDec);
        String name = identifierNode.getText();
        Type type = getVarType(baseType, varDec);
        if (this.globalScope.lookupCurrentVariable(name) != null) {
            errors.add(SplcSemanticError.redefinition(identifierNode));
        } else {
            this.globalScope.defineVariable(name, type);
        }
    }

    private void visitFuncDef(week8Parser.GlobalDefContext ctx) {
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
            for (week8Parser.StatementContext statement : ctx.statement()) {
                visit(statement);   //手动visit
            }
        } finally {
            currentScope = enclosingScope;
        }
    }
    private List<VariableSymbol> collectParameters(week8Parser.FuncArgsContext ctx) {
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

    @Override
    public Void visitCombineStmt(week8Parser.CombineStmtContext ctx) {
        Scope enclosingScope = currentScope;
        currentScope = new Scope(enclosingScope);
        try {
            for (week8Parser.StatementContext statement : ctx.statement()) {
                visit(statement);
            }
        } finally {
            currentScope = enclosingScope;
        }
        return null;
    }

    @Override
    public Void visitLocalVariableStmt(week8Parser.LocalVariableStmtContext ctx) {
        Type baseType = resolveType(ctx.specifier());
        week8Parser.VarDecContext varDec = ctx.varDec();
        TerminalNode identifier = getVarIdentifier(varDec);
        Type variableType = getVarType(baseType, varDec);
        VariableSymbol variable = currentScope.defineVariable(identifier.getText(), variableType);
        
        if (variable == null) {
            errors.add(SplcSemanticError.redefinition(identifier));
        } else {
            checkArrayInitializer(identifier, variableType, ctx.expression());
        }

        if (ctx.expression() != null) {
            visit(ctx.expression());
        }
        return null;
    }

    @Override
    public Void visitExpression(week8Parser.ExpressionContext ctx) {
        if (ctx.ASSIGN() != null && ctx.expression().size() == 2) {
            week8Parser.ExpressionContext left = ctx.expression(0);
            week8Parser.ExpressionContext right = ctx.expression(1);

            TerminalNode identifier = getAssignedVariable(left);
            Type leftType = inferExpressionType(left);
            Type rightType = inferExpressionType(right);

            if (identifier != null
                    && leftType instanceof ArrayType
                    && rightType != null
                    && !(rightType instanceof ArrayType)) {
                errors.add(SplcSemanticError.incompatibleType(
                        identifier, leftType, rightType));
            }
        }
        return visitChildren(ctx);
    }

    private void checkArrayInitializer(
            TerminalNode identifier,
            Type variableType,
            week8Parser.ExpressionContext expression) {
        if (expression == null || !(variableType instanceof ArrayType)) {
            return;
        }

        Type valueType = inferExpressionType(expression);
        if (valueType != null && !(valueType instanceof ArrayType)) {
            errors.add(SplcSemanticError.incompatibleType(
                    identifier, variableType, valueType));
        }
    }

    private Type inferExpressionType(week8Parser.ExpressionContext ctx) {
        if (ctx == null) {
            return null;
        }

        if (ctx.getChildCount() == 1 && ctx.Identifier() != null) {
            VariableSymbol variable = currentScope.lookupVariable(
                    ctx.Identifier().getText());
            return variable == null ? null : variable.getType();
        }

        if (ctx.NUMBER() != null) {
            return PrimitiveType.INT;
        }

        if (ctx.CHAR_VALUE() != null) {
            return PrimitiveType.CHAR;
        }

        if (ctx.LPAREN() != null && ctx.expression().size() == 1) {
            return inferExpressionType(ctx.expression(0));
        }

        if (ctx.LBRACK() != null && ctx.expression().size() == 2) {
            Type containerType = inferExpressionType(ctx.expression(0));
            if (containerType instanceof ArrayType) {
                return ((ArrayType) containerType).getElementType();
            }
            return null;
        }

        if (ctx.ASSIGN() != null && ctx.expression().size() == 2) {
            return inferExpressionType(ctx.expression(1));
        }

        if (ctx.expression().size() == 2) {
            return PrimitiveType.INT;
        }

        return null;
    }

    private TerminalNode getAssignedVariable(week8Parser.ExpressionContext ctx) {
        if (ctx.getChildCount() == 1 && ctx.Identifier() != null) {
            return ctx.Identifier();
        }

        if (ctx.LPAREN() != null && ctx.expression().size() == 1) {
            return getAssignedVariable(ctx.expression(0));
        }

        if (ctx.LBRACK() != null && ctx.expression().size() == 2) {
            return getAssignedVariable(ctx.expression(0));
        }

        return null;
    }

    private TerminalNode getVarIdentifier(week8Parser.VarDecContext ctx) {
        if (ctx.Identifier() != null) {
            return ctx.Identifier();
        } else if (ctx.varDec() != null) {
            return getVarIdentifier(ctx.varDec());
        } else {
            throw new IllegalArgumentException("Invalid variable declaration context");
        }
    }

    private Type getVarType(Type baseType, week8Parser.VarDecContext ctx) {
        if (ctx.Identifier() != null) {
            return baseType;
        }

        List<Integer> dims = new ArrayList<>();
        week8Parser.VarDecContext p = ctx;
        while (p.Identifier() == null) {
            dims.add(Integer.parseInt(p.NUMBER().getText())); 
            p = p.varDec();
        }
        
        Type t = baseType;
        for (int d : dims) {
            t = new ArrayType(t, d);
        }
        return t;
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