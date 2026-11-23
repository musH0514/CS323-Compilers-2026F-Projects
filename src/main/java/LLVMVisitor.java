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
        return null;
    }

    @Override
    public Val visitAssign(week12Parser.AssignContext ctx) {
        //todo: invoke builder.store
        return null;
    }

    @Override
    public Val visitRetStmt(week12Parser.RetStmtContext ctx) {
        //todo: invoke builder.ret
        return null;
    }

    @Override
    public Val visitMulDiv(week12Parser.MulDivContext ctx) {
        //todo: finish mul and div
        return null;
    }

    @Override
    public Val visitAddSub(week12Parser.AddSubContext ctx) {
        //todo: finish add and sub
        return null;
    }

    @Override
    public Val visitVar(week12Parser.VarContext ctx) {
        //todo: invoke builder.load
        return null;
    }

    @Override
    public Val visitNum(week12Parser.NumContext ctx) {
        int value = Integer.parseInt(ctx.NUMBER().getText());
        return Val.of(value);
    }

    @Override
    public Val visitCompare(week12Parser.CompareContext ctx) {
       //todo: finish compare
        return null;
    }

    @Override
    public Val visitParen(week12Parser.ParenContext ctx) {
        return visit(ctx.expr());
    }
}