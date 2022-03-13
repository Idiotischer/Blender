package com.dragoncommissions.mixbukkit.api.shellcode.impl.inner;

import com.dragoncommissions.mixbukkit.api.shellcode.LocalVarManager;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCode;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCodeInfo;
import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@ShellCodeInfo(
        name = "Reflection Method Invoke",
        description = "Invoke a method with similar usage as ShellCodeMethodInvoke",
        stacksContent = {"Return value of invoked method"},
        requiredStacksContent = {"Object that calls the method", "Arguments (in order)"}
)
@AllArgsConstructor
@Getter
public class ShellCodeReflectionMethodInvoke extends ShellCode {

    private Method method;

    @SneakyThrows
    private static void action() {
        Class.forName("CLASS_NAME_HERE", true, Bukkit.getPluginManager().getPlugins()[0].getClass().getClassLoader())
                .getDeclaredMethod("methodNameHere", int.class, float.class, double.class, char.class, boolean.class, byte.class, short.class, String.class, String[].class, int[].class)
                .invoke(null);
    }

    @Override
    @SneakyThrows
    public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
        InsnList out = new InsnList();
        List<Integer> args = new ArrayList<>();
        int obj = -1;
        if (!Modifier.isStatic(method.getModifiers())) {
            obj = varManager.allocateVarNumber();
            out.add(new VarInsnNode(Opcode.ASTORE, obj));
        }
        int length = method.getParameterTypes().length;
        for (int i = 0; i < length; i++) {
            int arg = varManager.allocateVarNumber();
            args.add(arg);
            out.add(new VarInsnNode(Opcode.ASTORE, arg));
        }

        out.add(new LdcInsnNode(method.getDeclaringClass().getName().replace(".", "/")));
        out.add(new ShellCodePushInt(1).generate()); // true
        out.add(new ShellCodeMethodInvoke(Class.class.getDeclaredMethod("forName", String.class, boolean.class, ClassLoader.class)).generate());
        out.add(new LdcInsnNode(method.getName()));
        out.add(new ShellCodeNewArrayAndAddContent(method.getParameterTypes().length, Class.class, index -> {
            InsnList list = new InsnList();
            list.add(ASMUtils.generateGetClassNode(method.getParameterTypes()[index]));
            return list;
        }).generate());
        out.add(new ShellCodeMethodInvoke(Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class)).generate());
        if (obj == -1) {
            out.add(new InsnNode(Opcode.ACONST_NULL));
        } else {
            out.add(new VarInsnNode(Opcode.ALOAD, obj));
        }
        out.add(new ShellCodeNewArrayAndAddContent(args.size(), Object.class, index -> {
            InsnList list = new InsnList();
            out.add(new VarInsnNode(Opcode.ASTORE, args.get(index)));
            return list;
        }).generate());
        out.add(new ShellCodeMethodInvoke(Method.class.getDeclaredMethod("invoke", Object.class, Object[].class)).generate());

        return out;
    }
}
