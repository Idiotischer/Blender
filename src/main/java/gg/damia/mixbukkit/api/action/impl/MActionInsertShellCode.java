package gg.damia.mixbukkit.api.action.impl;

import gg.damia.mixbukkit.api.action.MixinAction;
import gg.damia.mixbukkit.api.locator.HookLocator;
import gg.damia.mixbukkit.api.shellcode.LocalVarManager;
import gg.damia.mixbukkit.api.shellcode.ShellCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

import static org.objectweb.asm.Opcodes.INVOKESPECIAL;

@AllArgsConstructor
@Getter
public class MActionInsertShellCode implements MixinAction {

    private ShellCode shellCode;
    private HookLocator hookLocator;

    @Override
    public void action(Class<?> owner, MethodNode method) {
        boolean isConstructor = method.name.equals(owner.getSimpleName());
        if (!isConstructor) {
            insertShellCodeIntoMethod(method);
            return;
        }

        insertShellCodeIntoConstructor(method, owner);
    }

    private void insertShellCodeIntoMethod(MethodNode method) {
        List<Integer> hooks = hookLocator.getLineNumber(method.instructions);
        LocalVarManager localVarManager = new LocalVarManager(method);

        // Hook!
        InsnList newInstructions = new InsnList();

        for (int i = 0; i < method.instructions.size(); i++) {
            if (hooks.contains(i)) {
                if (shellCode.getShellCodeInfo().calledDirectly()) {
                    try {
                        InsnList instructions = shellCode.generate(method, localVarManager);
                        newInstructions.add(instructions);

                        newInstructions.add(shellCode.popExtraStack());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Shell Code \"" + ChatColor.YELLOW + shellCode.getShellCodeInfo().name() + ChatColor.RED + "\" has failed generating instructions: Exception Thrown");
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Shell Code \"" + ChatColor.YELLOW + shellCode.getShellCodeInfo().name() + ChatColor.RED + "\" shouldn't be called directly (calledDirectly = false)");
                }
            }

            newInstructions.add(method.instructions.get(i));
        }

        method.instructions = newInstructions;
    }

    private void insertShellCodeIntoConstructor(MethodNode method, Class<?> owner) {
        List<Integer> hooks = hookLocator.getLineNumber(method.instructions);
        LocalVarManager localVarManager = new LocalVarManager(method);

        InsnList newInstructions = new InsnList();
        boolean superCalled = false;

        for (int i = 0; i < method.instructions.size(); i++) {
            AbstractInsnNode insn = method.instructions.get(i);

            if (!superCalled && insn.getOpcode() == INVOKESPECIAL) {
                MethodInsnNode mInsn = (MethodInsnNode) insn;
                if (mInsn.name.equals("<init>") && !mInsn.owner.equals(owner.getName().replace('.', '/'))) {
                    superCalled = true;
                }
            }

            newInstructions.add(insn);

            if (superCalled && hooks.contains(i)) {
                if (shellCode.getShellCodeInfo().calledDirectly()) {
                    try {
                        InsnList instructions = shellCode.generate(method, localVarManager);
                        newInstructions.add(instructions);
                        newInstructions.add(shellCode.popExtraStack());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Shell Code \"" + ChatColor.YELLOW + shellCode.getShellCodeInfo().name() + ChatColor.RED + "\" has failed generating instructions: Exception Thrown");
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Shell Code \"" + ChatColor.YELLOW + shellCode.getShellCodeInfo().name() + ChatColor.RED + "\" shouldn't be called directly (calledDirectly = false)");
                }
            }
        }

        method.instructions = newInstructions;
    }

}