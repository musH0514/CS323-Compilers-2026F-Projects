import generated.week12.week12BaseVisitor;
import generated.week12.week12Parser;
import llvm.Addr;
import llvm.Builder;
import llvm.Val;

import java.util.HashMap;
import java.util.Map;

public class LLVMVisitor extends week12BaseVisitor<Val> {
    private final Builder builder = new Builder();
    private final Map<String, Addr> vars = new HashMap<>();

    public String getLLVM() {
        return builder.print();
    }

    @Override
    public Val visitFunction(week12Parser.FunctionContext ctx) {
        ctx.block().stmt().forEach(this::visit);
        return null;
    }

    @Override
    public Val visitVarDecl(week12Parser.VarDeclContext ctx) {
        //todo:  invoke builder.store and builder.newVar
        Addr addr = builder.newVar(ctx.Identifier().getText());
        vars.put(ctx.Identifier().getText(), addr);
        if (ctx.expr() != null) {
            Val v = visit(ctx.expr());
            builder.store(addr, v);
            return v;
        }
        return null;
    }

    @Override
    public Val visitAssign(week12Parser.AssignContext ctx) {
        //todo: invoke builder.store
        Addr addr = vars.get(ctx.Identifier().getText());   //去符号表里面找
        if (addr == null) {
            throw new RuntimeException("Undeclared variable: " + ctx.Identifier().getText());
        }
        Val v = visit(ctx.expr());
        builder.store(addr, v);
        return v;
    }

    @Override
    public Val visitRetStmt(week12Parser.RetStmtContext ctx) {
        //todo: invoke builder.ret
        Val v = visit(ctx.expr());
        builder.ret(v);
        return v;
    }

    @Override
    public Val visitMulDiv(week12Parser.MulDivContext ctx) {
        //todo: finish mul and div
        if (ctx.STAR() != null && ctx.expr().size() == 2) {
            return builder.mul(visit(ctx.expr(0)), visit(ctx.expr(1)));
        } else if (ctx.DIV() != null && ctx.expr().size() == 2) {
            return builder.div(visit(ctx.expr(0)), visit(ctx.expr(1)));
        } else {
            throw new RuntimeException("Invalid MulDiv expression");
        }
    }

    @Override
    public Val visitAddSub(week12Parser.AddSubContext ctx) {
        //todo: finish add and sub
        if (ctx.PLUS() != null && ctx.expr().size() == 2) {
            return builder.add(visit(ctx.expr(0)), visit(ctx.expr(1)));
        } else if (ctx.MINUS() != null && ctx.expr().size() == 2) {
            return builder.sub(visit(ctx.expr(0)), visit(ctx.expr(1)));
        } else {
            throw new RuntimeException("Invalid AddSub expression");
        }
    }

    @Override
    public Val visitVar(week12Parser.VarContext ctx) {
        //todo: invoke builder.load
        String name = ctx.Identifier().getText();
        Addr addr = vars.get(name);
        if (addr == null) {
            throw new RuntimeException("Undeclared variable: " + name);
        }
        Val v = builder.load(addr, name);
        return v;
    }

    @Override
    public Val visitNum(week12Parser.NumContext ctx) {
        int value = Integer.parseInt(ctx.NUMBER().getText());
        return Val.of(value);
    }

    @Override
    public Val visitCompare(week12Parser.CompareContext ctx) {
       //todo: finish compare
        Val v1 = visit(ctx.expr(0));
        Val v2 = visit(ctx.expr(1));
        if (ctx.EQ() != null) {
            return builder.cmp("eq", v1, v2);
        } else if (ctx.LT() != null) {
            return builder.cmp("slt", v1, v2);
        } else if (ctx.GT() != null) {
            return builder.cmp("sgt", v1, v2);
        } else if (ctx.LE() != null) {
            return builder.cmp("sle", v1, v2);
        } else if (ctx.GE() != null) {
            return builder.cmp("sge", v1, v2);
        } else {
            throw new RuntimeException("Invalid Compare expression");
        }
    }

    @Override
    public Val visitParen(week12Parser.ParenContext ctx) {
        return visit(ctx.expr());
    }
}