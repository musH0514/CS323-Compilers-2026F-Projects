; ModuleID = 'week11.c'
source_filename = "week11.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-i128:128-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @finalPrice(i32 noundef %unitPrice, i32 noundef %quantity) #0 {
entry:
  %unitPrice.addr = alloca i32, align 4
  %quantity.addr = alloca i32, align 4
  %amount = alloca i32, align 4
  store i32 %unitPrice, ptr %unitPrice.addr, align 4
  store i32 %quantity, ptr %quantity.addr, align 4
  %0 = load i32, ptr %unitPrice.addr, align 4
  %1 = load i32, ptr %quantity.addr, align 4
  %mul = mul nsw i32 %0, %1
  store i32 %mul, ptr %amount, align 4
  %2 = load i32, ptr %amount, align 4
  %add = add nsw i32 %2, 9
  store i32 %add, ptr %amount, align 4
  %3 = load i32, ptr %amount, align 4
  %sub = sub nsw i32 %3, 4
  store i32 %sub, ptr %amount, align 4
  %4 = load i32, ptr %amount, align 4
  ret i32 %4
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @main() #0 {
entry:
  %retval = alloca i32, align 4
  store i32 0, ptr %retval, align 4
  %call = call i32 @finalPrice(i32 noundef 5, i32 noundef 3)
  ret i32 %call
}

attributes #0 = { noinline nounwind optnone uwtable "frame-pointer"="all" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cmov,+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }

!llvm.module.flags = !{!0, !1, !2, !3, !4}
!llvm.ident = !{!5}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{i32 8, !"PIC Level", i32 2}
!2 = !{i32 7, !"PIE Level", i32 2}
!3 = !{i32 7, !"uwtable", i32 2}
!4 = !{i32 7, !"frame-pointer", i32 2}
!5 = !{!"Debian clang version 19.1.7 (3+b1)"}
