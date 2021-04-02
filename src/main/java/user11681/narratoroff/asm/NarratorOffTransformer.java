package user11681.narratoroff.asm;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.gudenau.lib.unsafe.Unsafe;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.transformers.MixinClassWriter;

public class NarratorOffTransformer implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    static {
        try {
            ClassNode klass = new ClassNode();
            new ClassReader(NarratorOffTransformer.class.getResourceAsStream("/com/mojang/text2speech/Narrator.class")).accept(klass, 0);

            for (MethodNode method : klass.methods) {
                if (method.name.equals("getNarrator")) {
                    method.instructions.clear();
                    method.tryCatchBlocks = null;

                    method.visitTypeInsn(Opcodes.NEW, "com/mojang/text2speech/NarratorDummy");
                    method.visitInsn(Opcodes.DUP);
                    method.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/mojang/text2speech/NarratorDummy", "<init>", "()V", false);
                    method.visitInsn(Opcodes.ARETURN);

                    break;
                }
            }

            ClassWriter writer = new MixinClassWriter(ClassWriter.COMPUTE_FRAMES);
            klass.accept(writer);

            byte[] bytecode = writer.toByteArray();
            Unsafe.defineClass("com.mojang.text2speech.Narrator", bytecode, 0, bytecode.length, ClassLoader.getSystemClassLoader(), null);
        } catch (IOException exception) {
            throw Unsafe.throwException(exception);
        }
    }
}
