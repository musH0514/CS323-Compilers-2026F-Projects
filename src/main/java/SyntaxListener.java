import generated.week8.week8BaseListener;
import generated.week8.week8Parser;
import impl.Scope;
import impl.SplcSemanticError;
import impl.symbol.VariableSymbol;
import impl.type.PrimitiveType;
import impl.type.Type;
import impl.type.ArrayType;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

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

    @Override
    public void exitGlobalDef(week8Parser.GlobalDefContext ctx) {
        if (ctx.LBRACE() != null && ctx.RBRACE() != null) {
            currentScope = currentScope.getParent();
        }
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
        Type returnType = resolveType(ctx.specifier());
        String name = ctx.Identifier().getText();
        if (globalScope.lookupFunction(name) == null) {
            globalScope.defineFunction(name, returnType, new ArrayList<>());
            currentScope = globalScope.lookupFunction(name);   //renew current scope
        } else {
            errors.add(SplcSemanticError.redeclaration(ctx.Identifier()));
            return;
        }
        
        List<VariableSymbol> parameters = collectParameters(ctx.funcArgs());
        Scope enclosingScope = currentScope;
        Scope function = currentScope.lookupFunction(name);
        currentScope = function;
        try {
            for (VariableSymbol parameter : parameters) {
                currentScope.defineVariable(parameter.getName(), parameter.getType());
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
    public void enterCombineStmt(week8Parser.CombineStmtContext ctx) {
        currentScope = new Scope(currentScope);
    }

    @Override
    public void exitCombineStmt(week8Parser.CombineStmtContext ctx) {
        currentScope = currentScope.getParent();
    }

    @Override
    public void enterLocalVariableStmt(week8Parser.LocalVariableStmtContext ctx) {
        Type baseType = resolveType(ctx.specifier());
        week8Parser.VarDecContext varDec = ctx.varDec();
        TerminalNode identifier = getVarIdentifier(varDec);
        Type variableType = getVarType(baseType, varDec);
        VariableSymbol variable = currentScope.defineVariable(identifier.getText(), variableType);

        if (variable == null) {
            errors.add(SplcSemanticError.redefinition(identifier));
            return;
        }

        if (ctx.expression() != null) {
            checkArrayInitializer(identifier, variableType, ctx.expression());
        }
    }

    private void checkArrayInitializer(
        TerminalNode identifier,
        Type variableType,
        week8Parser.ExpressionContext expression) {

        if (!(variableType instanceof ArrayType)) {
            return;
        }

        Type valueType = inferExpressionType(expression);
        if (valueType != null && !(valueType instanceof ArrayType)) {   //检查类型是否匹配
            errors.add(SplcSemanticError.incompatibleType(identifier, variableType, valueType));
        }
    }

    @Override
    public void enterExpression(week8Parser.ExpressionContext ctx) {    //判断赋值表达式
        if (ctx.ASSIGN() == null || ctx.expression().size() != 2) {
            return;
        }

        week8Parser.ExpressionContext left = ctx.expression(0);
        week8Parser.ExpressionContext right = ctx.expression(1);

        TerminalNode identifier = getAssignedVariable(left);
        Type leftType = inferExpressionType(left);
        Type rightType = inferExpressionType(right);

        if (identifier != null
            && leftType instanceof ArrayType
            && rightType != null
            && !(rightType instanceof ArrayType)) {
            errors.add(SplcSemanticError.incompatibleType(identifier, leftType, rightType));
        }
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

    private Type inferExpressionType(week8Parser.ExpressionContext ctx) {
        if (ctx == null) {
            return null;
        }

        if (ctx.getChildCount() == 1 && ctx.Identifier() != null) {
            VariableSymbol variable = currentScope.lookupVariable(ctx.Identifier().getText());
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
        while (p.Identifier() == null) {    // The elements in dims are [3, 2, 1]
            dims.add(Integer.parseInt(p.NUMBER().getText())); 
            p = p.varDec();
        }
        
        // Origninal: t = int
        // After first loop:  t = array(int, 3)
        // After second loop: t = array(array(int, 3), 2)
        // After third loop:  t = array(array(array(int, 3), 2), 1) (toString int[3][2][1])
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