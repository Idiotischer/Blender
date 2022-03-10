package com.dragoncommissions.mixbukkit.api.handler;

import com.dragoncommissions.mixbukkit.utils.ASMUtils;
import javassist.bytecode.Opcode;
import org.bukkit.Bukkit;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class MHandlerMethod implements MixinHandler {

    private Method handler;
    private boolean isTargetStatic;

    public MHandlerMethod(Method handler, boolean isTargetStatic) {
        this.handler = handler;
        this.isTargetStatic = isTargetStatic;
    }

    @Override
    public AbstractInsnNode[] getInstructions(MethodNode method, int latestVarCount) {
        List<AbstractInsnNode> out = new ArrayList<>();
        latestVarCount++;
        int varNumOf11 = latestVarCount++;
        int varNumOf12 = latestVarCount++;
        int varNumOf13 = latestVarCount++;
        int varNumOf14 = latestVarCount++;
        int varNumOf15 = latestVarCount++;
        int varNumOf16 = latestVarCount++;
        int varNumOf17 = latestVarCount++;

        LabelNode label0 = new LabelNode();
        LabelNode label1 = new LabelNode();
        LabelNode label2 = new LabelNode();
        LabelNode label3 = new LabelNode();
        LabelNode label4 = new LabelNode();
        LabelNode label5 = new LabelNode();
        LabelNode label6 = new LabelNode();
        LabelNode label7 = new LabelNode();
        LabelNode label8 = new LabelNode();
        LabelNode label9 = new LabelNode();
        LabelNode label10 = new LabelNode();

        out.add(label0);
        out.add(new MethodInsnNode(184, "org/bukkit/Bukkit", "getPluginManager", "()Lorg/bukkit/plugin/PluginManager;", false));
        out.add(new MethodInsnNode(185, "org/bukkit/plugin/PluginManager", "getPlugins", "()[Lorg/bukkit/plugin/Plugin;", true));
        out.add(new VarInsnNode(58, varNumOf11));
        out.add(new VarInsnNode(25, varNumOf11));
        out.add(new InsnNode(190));
        out.add(new VarInsnNode(54, varNumOf12));
        out.add(new InsnNode(3));
        out.add(new VarInsnNode(54, varNumOf13));
        out.add(label1);
//        out.add(new FrameNode(1, 3, new Object[] {"[Lorg/bukkit/plugin/Plugin;", 1, 1}, 0, null));
        out.add(new VarInsnNode(21, varNumOf13));
        out.add(new VarInsnNode(21, varNumOf12));
        out.add(new JumpInsnNode(162, label7));
        out.add(new VarInsnNode(25, varNumOf11));
        out.add(new VarInsnNode(21, varNumOf13));
        out.add(new InsnNode(50));
        out.add(new VarInsnNode(58, varNumOf14));
        out.add(label2);
        out.add(new VarInsnNode(25, varNumOf14));
        out.add(new MethodInsnNode(182, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
        out.add(new MethodInsnNode(182, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false));
        out.add(new VarInsnNode(58, varNumOf15));
        out.add(label3);
        out.add(new LdcInsnNode(handler.getDeclaringClass().getName()));
        out.add(new InsnNode(4));
        out.add(new VarInsnNode(25, varNumOf15));
        out.add(new MethodInsnNode(184, "java/lang/Class", "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false));
        out.add(new VarInsnNode(58, varNumOf16));
        out.add(label4);
        out.add(new VarInsnNode(25, varNumOf16));
        out.add(new LdcInsnNode(handler.getName()));
        out.add(ASMUtils.pushInt(handler.getParameterTypes().length));
        out.add(new TypeInsnNode(189, "java/lang/Class"));
        for (int i = 0; i < handler.getParameterTypes().length; i++) {
            out.add(new InsnNode(Opcode.DUP));
            out.add(ASMUtils.pushInt(i));
            out.add(ASMUtils.generateGetClassNode(handler.getParameterTypes()[i]));
            out.add(new InsnNode(Opcode.AASTORE));
        }
        out.add(new MethodInsnNode(182, "java/lang/Class", "getDeclaredMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false));
        out.add(new VarInsnNode(58, varNumOf17));
        out.add(label5);
        out.add(new VarInsnNode(25, varNumOf17));
        out.add(new InsnNode(1));
        out.add(ASMUtils.pushInt(handler.getParameterTypes().length));
        out.add(new TypeInsnNode(189, "java/lang/Object"));
        int baseNumber = isTargetStatic?0:1;
        for (int i = 0; i < handler.getParameterTypes().length; i++) {
            out.add(new InsnNode(Opcode.DUP));
            out.add(ASMUtils.pushInt(i));
            InsnList list = ASMUtils.castToObject(baseNumber + i, handler.getParameterTypes()[i]);
            for (AbstractInsnNode insnNode : list) {
                out.add(insnNode);
            }
            out.add(new InsnNode(Opcode.AASTORE));
        }
        out.add(new MethodInsnNode(182, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false));
        out.add(new InsnNode(87));
        out.add(label6);
        out.add(new JumpInsnNode(167, label7));
        out.add(label7);
//        out.add(new FrameNode(2, 3, new Object[] {null, null, null}, 0, null));
        out.add(new JumpInsnNode(167, label9));
        out.add(label8);
//        out.add(new FrameNode(4, 0, null, 1, new Object[] {"java/lang/Exception"}));
        out.add(new VarInsnNode(58, varNumOf11));
        out.add(label9);
//        out.add(new FrameNode(3, 0, null, 0, null));
//        method.invisibleLocalVariableAnnotations
        method.tryCatchBlocks.add(new TryCatchBlockNode(label0, label7, label8, "java/lang/Exception"));
        method.localVariables.add(new LocalVariableNode("classLoader", "Ljava/lang/ClassLoader;", null, label1, label2, varNumOf15));
        method.localVariables.add(new LocalVariableNode("loadedClass", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", label3, label2, varNumOf16));
        method.localVariables.add(new LocalVariableNode("methodNameHere", "Ljava/lang/reflect/Method;", null, label4, label2, varNumOf17));
        method.localVariables.add(new LocalVariableNode("plugin", "Lorg/bukkit/plugin/Plugin;", null, label5, label2, varNumOf14));
        return out.toArray(new AbstractInsnNode[0]);
    }
}
