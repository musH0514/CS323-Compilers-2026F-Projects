import generated.week6.week6Parser;
import generated.week6.week6BaseVisitor;

public class CalcLab5Visitor extends week6BaseVisitor<Double> {
    public CalcLab5Visitor() {}

    public Double visit(week6Parser.ExprContext expr) {
        return super.visit(expr);
    }

    @Override
    public Double visitAren(week6Parser.ArenContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Double visitMulDiv(week6Parser.MulDivContext ctx) {
        double left = visit(ctx.expr(0));    //整个expr中的第一个expr
        double right = visit(ctx.expr(1));

        if (ctx.MUL() != null) {
            return left * right;
        }
        if (right == 0) {
            throw new ArithmeticException("Cannot be divided by 0");
        }
        return left / right;
    }

    @Override
    public Double visitAddSub(week6Parser.AddSubContext ctx) {
        double left = visit(ctx.expr(0));
        double right = visit(ctx.expr(1));

        if (ctx.ADD() != null) {
            return left + right;
        }
        return left - right;
    }

    @Override
    public Double visitGoFactor(week6Parser.GoFactorContext ctx) {
        return Double.parseDouble(ctx.factor().getText());
    }
}