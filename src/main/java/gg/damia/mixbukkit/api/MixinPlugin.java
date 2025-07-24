package gg.damia.mixbukkit.api;

import gg.damia.mixbukkit.MixBukkit;
import gg.damia.mixbukkit.agent.ClassesManager;
import gg.damia.mixbukkit.api.action.MixinAction;
import gg.damia.mixbukkit.utils.ASMUtils;
import gg.damia.mixbukkit.utils.ConflictChecker;
import gg.damia.mixbukkit.utils.CustomTextifier;
import lombok.Getter;
import lombok.SneakyThrows;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.inline.DecoratingDynamicTypeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.instrument.ClassDefinition;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MixinPlugin {

    @Getter
    private final ObfMap obfMap;
    @Getter
    private final Plugin plugin;

    private final List<String> registeredMixins = new ArrayList<>();

    public MixinPlugin(Plugin plugin, ObfMap obfMap) {
        this.plugin = plugin;
        this.obfMap = obfMap;
    }

    @SneakyThrows
    public Method getMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... arguments) {
        String original = obfMap.resolveMapping(new ObfMap.MethodMapping(clazz.getName().replace(".", "/"), ASMUtils.getDescriptor(returnType, arguments), methodName));
        return clazz.getDeclaredMethod(original, arguments);
    }


    /**
     * if this method is used the mixinClasses's inheritance will be copied over to the mc class like in sponge mixins
     * still in beta, though I need to look at how i will make it so the classes and methods (like in methodreplacer) don't
     * double, triple etc.
     */
    @SneakyThrows
    public boolean registerMixin(String namespace, Class<?> mixinClass, MixinAction mixinAction, Class<?> owner, String deObfMethodName, Class<?> returnType, Class<?>... arguments) {
        return registerMixin(namespace, mixinClass, mixinAction, owner, deObfMethodName, returnType, true, false, arguments);
    }
    @SneakyThrows
    public boolean registerMixin(String namespace, Class<?> mixinClass, MixinAction mixinAction, Class<?> owner, String deObfMethodName, Class<?> returnType, boolean fieldInjection, Class<?>... arguments) {
        return registerMixin(namespace, mixinClass, mixinAction, owner, deObfMethodName, returnType, fieldInjection,false, arguments);
    }

    /**
     * if this method is used the mixinClasses's inheritance will be copied over to the mc class like in sponge mixins
     * still in beta, though I need to look at how i will make it so the classes and methods (like in methodreplacer) don't
     * double, triple etc.
     */
    @SneakyThrows
    public boolean registerMixin(String namespace, Class<?> mixinClass, MixinAction mixinAction, Class<?> owner, String deObfMethodName, Class<?> returnType, boolean fieldInjection, boolean canSafelyReplaceSuperClass, Class<?>... arguments) {
        if (registeredMixins.contains(namespace)) {
            if (MixBukkit.DEBUG) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Mixin with namespace: " + namespace + " is already registered! Skipping...");
            }
            return false;
        }

        if (MixBukkit.DEBUG) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Registering Mixin: " + plugin.getName() + ":" + namespace);
        }

        String descriptor = ASMUtils.getDescriptor(returnType, arguments);
        String obfMethodName = obfMap.resolveMapping(new ObfMap.MethodMapping(owner.getName().replace(".", "/"), descriptor, deObfMethodName));
        if (MixBukkit.DEBUG) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Obfuscated method name: " + owner.getName().replace(".", "/") + "." + obfMethodName + descriptor);
        }

        ClassNode classNode = ClassesManager.getClassNode(owner.getName());
        if (classNode == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not find the target class: " + owner.getName());
            return false;
        }


        for (Class<?> mixinInterface : mixinClass.getInterfaces()) {
            if (!Modifier.isPublic(mixinInterface.getModifiers())) {
                if (!mixinInterface.getPackage().equals(owner.getPackage())) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Cannot inject package-private interface: " + mixinInterface.getName() + " into " + owner.getName() + " as they are in different packages.");
                    continue;
                }
            }

            if (!ConflictChecker.isConflicting(owner, mixinInterface)) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Cannot inject interface: " + mixinInterface.getName() + " into " + owner.getName() + " due to missing required method implementations.");
                continue;
            }

            String interfaceInternalName = mixinInterface.getName().replace('.', '/');
            if (!classNode.interfaces.contains(interfaceInternalName)) {
                classNode.interfaces.add(interfaceInternalName);
                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Added interface " + mixinInterface.getName() + " to " + owner.getName());
                }
            }
        }

        ClassNode mixinNode = ClassesManager.getClassNode(mixinClass.getName());

        if (mixinNode != null && fieldInjection) {
            for (FieldNode mixinField : mixinNode.fields) {
                boolean exists = false;
                for (FieldNode ownerField : classNode.fields) {
                    if (ownerField.name.equals(mixinField.name) && ownerField.desc.equals(mixinField.desc)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    classNode.fields.add(mixinField);
                    if (MixBukkit.DEBUG) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Injected field " + mixinField.name + " into " + owner.getName());
                    }
                }
            }
        }


        if (!mixinClass.getSuperclass().equals(Object.class) && !mixinClass.getSuperclass().equals(owner.getSuperclass())) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[?] Mixin superclass is " + mixinClass.getSuperclass().getName() + ", but target superclass is " + owner.getSuperclass().getName() + ". Changing superclass at runtime is not supported and will be skipped. Maybe I'll try implementing it eventually and as a seperate registerMixin or with a boolean in the method");
        }

        PrintWriter printWriter = new PrintWriter(MixBukkit.ERROR_OUTPUT_STREAM, true);

        for (MethodNode method : classNode.methods) {
//            System.out.println(method.name + method.desc + "  /   " + obfMethodName + descriptor);
            if (method.name.equals(obfMethodName) && method.desc.equals(descriptor)) {
                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Found Method to hook!");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Processing...");
                }

                mixinAction.action(owner, method);

                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Assembling...");
                }

                byte[] data = ASMUtils.fromClassNode(classNode);

                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Verifying...");
                ClassReader classReader = new ClassReader(data);
                boolean[] illegal = new boolean[] {false};

                CheckClassAdapter.verify(classReader, getClass().getClassLoader().getParent(), false, new PrintWriter(new OutputStream() {
                    @Override
                    public void write(int b) {
                        illegal[0] = true;
                    }
                }));

                if (illegal[0]) {
                    if (MixBukkit.DEBUG) {
                        printWriter.println("Mixin Method:");
                        CustomTextifier methodVisitor = new CustomTextifier();
                        method.accept(methodVisitor);
                        for (Object o : methodVisitor.text) {
                            printWriter.println(o);
                        }
                        printWriter.println("");
                        printWriter.println("");
                        CheckClassAdapter.verify(classReader, getClass().getClassLoader().getParent(), false, printWriter);
                    }
                    if (MixBukkit.SAFE_MODE) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Invalid Bytecode, and safe-mode is on");
                        return false;
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[?] Mixin: " + plugin.getName() + ":" + namespace + " has failed the verification, and it might crash your server! Be careful.");
                    }
                }

                try {
                    if (MixBukkit.DEBUG) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Redefining class...");
                    }

                    MixBukkit.INSTRUMENTATION.redefineClasses(new ClassDefinition(owner, data));
                    ClassesManager.classNodes.put(owner.getName(), classNode);
                    ClassesManager.classes.put(owner.getName(), data);
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not redefine class: " + owner.getSimpleName());
                }

                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Successfully hooked " + namespace);
                }

                registeredMixins.add(namespace);
                return true;
            }
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not find the target method");
        return false;
    }

    @SneakyThrows
    @Deprecated
    public boolean registerMixin(String namespace, MixinAction mixinAction, Class<?> owner, String deObfMethodName, Class<?> returnType, Class<?>... arguments) {
        if (registeredMixins.contains(namespace)) {
            if (MixBukkit.DEBUG) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Mixin with namespace: " + namespace + " is already registered! Skipping...");
            }
            return false;
        }
        if (MixBukkit.DEBUG) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Registering Mixin: " + plugin.getName() + ":" + namespace);
        }
        String descriptor = ASMUtils.getDescriptor(returnType, arguments);
        String obfMethodName = obfMap.resolveMapping(new ObfMap.MethodMapping(owner.getName().replace(".", "/"), descriptor, deObfMethodName));
        if (MixBukkit.DEBUG) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Obfuscated method name: " + owner.getName().replace(".", "/") + "." + obfMethodName + descriptor);
        }

        ClassNode classNode = ClassesManager.getClassNode(owner.getName());
        if (classNode == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not find the target class: " + owner.getName());
            return false;
        }


        PrintWriter printWriter = new PrintWriter(MixBukkit.ERROR_OUTPUT_STREAM, true);
        for (MethodNode method : classNode.methods) {
//            System.out.println(method.name + method.desc + "  /   " + obfMethodName + descriptor);
            if (method.name.equals(obfMethodName) && method.desc.equals(descriptor)) {
                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Found Method to hook!");
                }

                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Processing...");
                }
                mixinAction.action(owner, method);

                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Assembling...");
                }
                byte[] data = ASMUtils.fromClassNode(classNode);

                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Verifying...");
                ClassReader classReader = new ClassReader(data);
                boolean[] illegal = new boolean[] {false};
                CheckClassAdapter.verify(classReader, getClass().getClassLoader().getParent(), false, new PrintWriter(new OutputStream() {
                    @Override
                    public void write(int b) {
                        illegal[0] = true;
                    }
                }));

                if (illegal[0]) {
                    if (MixBukkit.DEBUG) {
                        printWriter.println("Mixin Method:");
                        CustomTextifier methodVisitor = new CustomTextifier();
                        method.accept(methodVisitor);
                        for (Object o : methodVisitor.text) {
                            printWriter.println(o);
                        }
                        printWriter.println("");
                        printWriter.println("");
                        CheckClassAdapter.verify(classReader, getClass().getClassLoader().getParent(), false, printWriter);
                    }
                    if (MixBukkit.SAFE_MODE) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Invalid Bytecode, and safe-mode is on");
                        return false;
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[?] Mixin: " + plugin.getName() + ":" + namespace + " has failed the verification, and it might crash your server! Be careful.");
                    }
                }

                try {
                    if (MixBukkit.DEBUG) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Redefining class...");
                    }

                    MixBukkit.INSTRUMENTATION.redefineClasses(new ClassDefinition(owner, data));
                    ClassesManager.classNodes.put(owner.getName(), classNode);
                    ClassesManager.classes.put(owner.getName(), data);
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not redefine class: " + owner.getSimpleName());
                }
                if (MixBukkit.DEBUG) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Successfully hooked " + namespace);
                }
                registeredMixins.add(namespace);
                return true;
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[!] Failed to load mixin: " + plugin.getName() + ":" + namespace + ", Reason: Could not find the target method");
        return false;
    }

    public static void verify(@org.jetbrains.annotations.NotNull ClassReader classReader, ClassLoader loader, PrintWriter printWriter) {
        ClassNode classNode = new ClassNode();
        classReader.accept(new CheckClassAdapter(Opcodes.ASM9, classNode, true) {
        }, 2);
        Type syperType = classNode.superName == null ? null : Type.getObjectType(classNode.superName);
        List<MethodNode> methods = classNode.methods;
        List<Type> interfaces = new ArrayList<>();
        Iterator<?> var8 = classNode.interfaces.iterator();

        while(var8.hasNext()) {
            String interfaceName = (String)var8.next();
            interfaces.add(Type.getObjectType(interfaceName));
        }

        var8 = methods.iterator();

        while(var8.hasNext()) {
            MethodNode method = (MethodNode)var8.next();
            SimpleVerifier verifier = new SimpleVerifier(Type.getObjectType(classNode.name), syperType, interfaces, (classNode.access & 512) != 0);
            Analyzer<BasicValue> analyzer = new Analyzer<>(verifier);
            if (loader != null) {
                verifier.setClassLoader(loader);
            }

            try {
                analyzer.analyze(classNode.name, method);
            } catch (AnalyzerException var13) {
                var13.printStackTrace(printWriter);
            }

        }

        printWriter.flush();
    }

}
