package gg.damia.mixbukkit.api.action.impl;

import gg.damia.mixbukkit.api.action.MixinAction;
import gg.damia.mixbukkit.api.shellcode.LocalVarManager;
import gg.damia.mixbukkit.api.shellcode.impl.inner.IShellCodeReflectionMethodInvoke;
import gg.damia.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;

/**
 * THIS DOESN'T TAKE A CALLBACKINFO IN THE GETDECLAREDMETHOD
 **/
public class MActionMethodReplacer implements MixinAction {

    private Method handler;

    public MActionMethodReplacer(Method handler) {
        this.handler = handler;
    }

    @Override
    @SneakyThrows
    public void action(Class<?> owner, MethodNode methodNode) {
        methodNode.tryCatchBlocks.clear();
        methodNode.localVariables.clear();
        methodNode.instructions.clear();

        boolean isConstructor = methodNode.name.equals(owner.getSimpleName());

        LocalVarManager varManager = new LocalVarManager(methodNode);

        if (isConstructor) {
            Class<?>[] parameterTypes = handler.getParameterTypes();

            InsnList out = new InsnList();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                out.add(ASMUtils.castToObject(i, parameterType));
            }

            IShellCodeReflectionMethodInvoke shellCodeReflectionMethodInvoke = new IShellCodeReflectionMethodInvoke(handler);
            out.add(shellCodeReflectionMethodInvoke.generate(methodNode, varManager));

            out.add(new InsnNode(Opcode.RETURN));
            methodNode.instructions = out;
        } else {
            Class<?> returnType = ASMUtils.getReturnType(methodNode.desc);
            if (returnType != handler.getReturnType()) {
                throw new IllegalArgumentException("Handler: " + handler.getName() + " is not returning same type as target method (" + handler.getReturnType().getName() + "(return type of handler) != " + returnType.getName() + "(return type of target))");
            }

            InsnList out = new InsnList();
            Class<?>[] parameterTypes = handler.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                out.add(ASMUtils.castToObject(i, parameterType));
            }

            IShellCodeReflectionMethodInvoke shellCodeReflectionMethodInvoke = new IShellCodeReflectionMethodInvoke(handler);
            out.add(shellCodeReflectionMethodInvoke.generate(methodNode, varManager));

            if (!methodNode.desc.endsWith("V")) {
                out.add(ASMUtils.cast(returnType));
                out.add(ASMUtils.genReturnNode(returnType));
            } else {
                out.add(new InsnNode(Opcode.RETURN));
            }
            methodNode.instructions = out;
        }
    }

}
