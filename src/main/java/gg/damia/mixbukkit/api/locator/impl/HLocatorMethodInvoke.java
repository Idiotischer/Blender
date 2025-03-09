package gg.damia.mixbukkit.api.locator.impl;

import gg.damia.mixbukkit.api.locator.HookLocator;
import gg.damia.mixbukkit.utils.ASMUtils;
import gg.damia.mixbukkit.utils.PostPreState;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HLocatorMethodInvoke implements HookLocator {

    private final String owner;
    private final String desc;
    private final String name;
    private final PostPreState state;
    private final Predicate<Integer> filter;

    public HLocatorMethodInvoke(Method method, PostPreState state, Predicate<Integer> filter) {
        owner = method.getDeclaringClass().getName().replace(".", "/");
        desc = ASMUtils.getDescriptor(method.getReturnType(), method.getParameterTypes());
        name = method.getName();
        this.filter = filter;
        this.state = state;
    }

    public HLocatorMethodInvoke(Class<?> owner, Method method, PostPreState state, Predicate<Integer> filter) {
        this.owner = owner.getName().replace(".", "/");
        desc = ASMUtils.getDescriptor(method.getReturnType(), method.getParameterTypes());
        name = method.getName();
        this.filter = filter;
        this.state = state;
    }

    @Override
    public List<Integer> getLineNumber(InsnList insnList) {
        int amount = 0;
        List<Integer> out = new ArrayList<>();
        for (int i = 0; i < insnList.size(); i++) {
            if (insnList.get(i) instanceof MethodInsnNode) {
                MethodInsnNode insnNode = (MethodInsnNode) insnList.get(i);
                if (insnNode.owner.equals(owner) && insnNode.name.equals(name) && insnNode.desc.equals(desc) && filter.test(amount++)) {
                    out.add(i + (state == PostPreState.POST?1:0));
                }
            }
        }
        return out;
    }
}
