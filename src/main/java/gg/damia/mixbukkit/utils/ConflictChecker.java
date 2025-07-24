package gg.damia.mixbukkit.utils;

import gg.damia.mixbukkit.MixBukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.lang.reflect.Method;

public class ConflictChecker {
    public static Set<Class<?>> getInterfaces(Class<?> owner) {
        Set<Class<?>> result = new HashSet<>();
        collectInterfaces(owner, result);
        return result;
    }

    private static void collectInterfaces(Class<?> clazz, Set<Class<?>> result) {
        for (Class<?> interfaze : clazz.getInterfaces()) {
            if (result.add(interfaze)) {
                collectInterfaces(interfaze, result);
            }
        }
    }

    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            clazz.getMethod(methodName, paramTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean isConflicting(Class<?> owner, Class<?> iF) {
        Set<Class<?>> allInterfaces = getInterfaces(iF);
        allInterfaces.add(iF);

        for (Class<?> i : allInterfaces) {
            for (Method method : i.getMethods()) {
                if (!hasMethod(owner, method.getName(), method.getParameterTypes())) {
                    return false;
                }
            }
        }
        return true;
    }

    //for changing inheritance with extends blabla...
    // suche btw nen besseren name
    public static boolean isConflictExtends(Class<?> owner, @NotNull Class<?> superClass) {
        for (Method method : superClass.getMethods()) {
            if (Modifier.isAbstract(method.getModifiers())) {
                try {
                    Method ownerMethod = owner.getMethod(method.getName(), method.getParameterTypes());
                    if (Modifier.isAbstract(ownerMethod.getModifiers())) {
                        if (MixBukkit.DEBUG) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "Conflict: Method " + method + " is abstract in superclass and not implemented in " + owner.getName());
                        }

                        return true;
                    }
                } catch (NoSuchMethodException e) {
                    if (MixBukkit.DEBUG) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "Conflict: Missing implementation of abstract method " + method + " in class " + owner.getName());
                    }

                    return true;
                }
            }
        }

        boolean hasNoArgConstructor = false;
        Constructor<?>[] constructors = superClass.getDeclaredConstructors();
        for (Constructor<?> ctor : constructors) {
            if (ctor.getParameterCount() == 0 && Modifier.isPublic(ctor.getModifiers())) {
                hasNoArgConstructor = true;
                break;
            }
        }
        if (!hasNoArgConstructor) {
            if (MixBukkit.DEBUG) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "Conflict: Superclass " + superClass.getName() + " has no public no-arg constructor");
            }

            return true;
        }

        return false;
    }

}

//TODO: check if this is better, idk if i was cooking there lol
//
//public class ConflictChecker {
//    public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
//        Set<Class<?>> result = new HashSet<>();
//        collectInterfaces(clazz, result);
//        return result;
//    }
//
//    private static void collectInterfaces(Class<?> clazz, Set<Class<?>> result) {
//        for (Class<?> i : clazz.getInterfaces()) {
//            if (result.add(i)) {
//                collectInterfaces(i, result);
//            }
//        }
//    }
//    public static Set<MethodSignature> getAllMethods(Class<?> clazz) {
//        Set<MethodSignature> methods = new HashSet<>();
//        while (clazz != null) {
//            for (Method m : clazz.getDeclaredMethods()) {
//                methods.add(new MethodSignature(m));
//            }
//
//            for (Class<?> iface : clazz.getInterfaces()) {
//                for (Method m : iface.getMethods()) {
//                    methods.add(new MethodSignature(m));
//                }
//            }
//            clazz = clazz.getSuperclass();
//        }
//        return methods;
//    }
//
//    public static boolean implementsInterface(Class<?> target, Class<?> interfaceToCheck) {
//        Set<MethodSignature> targetMethods = getAllMethods(target);
//        Set<MethodSignature> interfaceMethods = new HashSet<>();
//
//        Set<Class<?>> allInterfaces = getAllInterfaces(interfaceToCheck);
//        allInterfaces.add(interfaceToCheck);
//        for (Class<?> i : allInterfaces) {
//            for (Method m : i.getMethods()) {
//                interfaceMethods.add(new MethodSignature(m));
//            }
//        }
//
//        return targetMethods.containsAll(interfaceMethods);
//    }
//
//TODO: rewrite
//
//    public static class MethodSignature {
//        private final String name;
//        private final List<Class<?>> paramTypes;
//
//        public MethodSignature(Method method) {
//            this.name = method.getName();
//            this.paramTypes = Arrays.asList(method.getParameterTypes());
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (!(o instanceof MethodSignature)) return false;
//            MethodSignature other = (MethodSignature) o;
//            return name.equals(other.name) && paramTypes.equals(other.paramTypes);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(name, paramTypes);
//        }
//    }
//}
//