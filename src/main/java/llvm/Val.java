package llvm;

public class Val {
    private final String name;   // 常量时为 null
    private final int constVal;

    private Val(String name, int constVal) {
        this.name = name;
        this.constVal = constVal;
    }

    public static Val of(int v) { return new Val(null, v); }

    public static Val ssa(String name) { return new Val(name, 0); }

    public String llvm() {
        return name == null ? String.valueOf(constVal) : name;
    }
}