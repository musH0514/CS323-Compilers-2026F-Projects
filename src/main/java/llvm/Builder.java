package llvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Builder {
    private final List<String> allocas = new ArrayList<>();
    private final List<String> insns = new ArrayList<>();

    // 每个变量已经被 load 过几次（alloca 本身记 0）
    private final Map<String, Integer> varCount = new HashMap<>();
    // 算术 / 比较临时变量的全局序号
    private int tmpCounter = 0;

    // ---- 局部变量 ----

    /**
     * 给一个新变量开栈槽。第一次出现直接用原名：%name，不加后缀。
     */
    public Addr newVar(String name) {
        varCount.put(name, 0);
        String n = "%" + name;
        allocas.add(n + " = alloca i32, align 4");
        return new Addr(n);
    }

    /**
     * 把值 v 写进变量 a。
     */
    public void store(Addr a, Val v) {
        insns.add("store i32 " + v.llvm() + ", ptr " + a.llvmName + ", align 4");
    }

    /**
     * 从变量 a 读出当前值 —— 这就是"左值转换"。
     * 同一个变量第 k 次被读出，命名为 %name1、%name2 ...
     */
    public Val load(Addr a, String hint) {
        int k = varCount.merge(hint, 1, Integer::sum);
        String n = "%" + hint + k;
        insns.add(n + " = load i32, ptr " + a.llvmName + ", align 4");
        return Val.ssa(n);
    }

    // ---- 算术（临时变量：%add0、%sub1 ...）----
    private String freshTmp(String hint) {
        return "%" + hint + (tmpCounter++);
    }

    public Val add(Val l, Val r) {
        String n = freshTmp("add");
        insns.add(n + " = add i32 " + l.llvm() + ", " + r.llvm());
        return Val.ssa(n);
    }

    public Val sub(Val l, Val r) {
        String n = freshTmp("sub");
        insns.add(n + " = sub i32 " + l.llvm() + ", " + r.llvm());
        return Val.ssa(n);
    }

    public Val mul(Val l, Val r) {
        String n = freshTmp("mul");
        insns.add(n + " = mul i32 " + l.llvm() + ", " + r.llvm());
        return Val.ssa(n);
    }

    public Val div(Val l, Val r) {
        String n = freshTmp("div");
        insns.add(n + " = sdiv i32 " + l.llvm() + ", " + r.llvm());
        return Val.ssa(n);
    }


    /**
     * @param op 取 "sgt" / "slt" / "sge" / "sle" / "eq"
     * @return 比较结果作为 int（0 或 1）
     */
    public Val cmp(String op, Val l, Val r) {
        String c = freshTmp("cmp");
        String z = freshTmp("zext");
        insns.add(c + " = icmp " + op + " i32 " + l.llvm() + ", " + r.llvm());
        insns.add(z + " = zext i1 " + c + " to i32");
        return Val.ssa(z);
    }

    // ---- 返回 ----
    public void ret(Val v) {
        insns.add("ret i32 " + v.llvm());
    }

    // ---- 出 .ll ----
    public String print() {
        StringBuilder sb = new StringBuilder("define i32 @main() {\nentry:\n");
        for (String a : allocas) sb.append("  ").append(a).append("\n");
        for (String i : insns) sb.append("  ").append(i).append("\n");
        return sb.append("}\n").toString();
    }
}