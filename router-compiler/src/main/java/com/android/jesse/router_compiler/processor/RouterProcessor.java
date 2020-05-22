package com.android.jesse.router_compiler.processor;

import com.android.jesse.router_annotation.Router;
import com.android.jesse.router_compiler.utils.Consts;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedAnnotationTypes(Consts.ANNOTATION_TYPE_ROUTER)
public class RouterProcessor extends BaseProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) return false;
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Router.class);
        for (Element element : elements) {
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            String className = element.getSimpleName().toString();
            Router router = element.getAnnotation(Router.class);
            logger.info("被注解的类有：" + className);
            //通过javaPoet生成类文件

            // 最终想生成的类文件名
            String finalClassName = className + "$$Router";

            // 高级写法，javapoet构建工具，参考（https://github.com/JakeWharton/butterknife）
            try {
                // 构建方法体
                MethodSpec method = MethodSpec.methodBuilder("findTargetClass") // 方法名
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(Class.class) // 返回值Class<?>
                        .addParameter(String.class, "path") // 参数(String path)
                        // 方法内容拼接：
                        // return path.equals("/app/MainActivity") ? MainActivity.class : null
                        .addStatement("return path.equals($S) ? $T.class : null",
                                router.path(), ClassName.get((TypeElement) element))
                        .build(); // 构建

                // 构建类
                TypeSpec type = TypeSpec.classBuilder(finalClassName)
                        .addModifiers(Modifier.PUBLIC) //, Modifier.FINAL)
                        .addMethod(method) // 添加方法体
                        .build(); // 构建

                // 在指定的包名下，生成Java类文件
                JavaFile javaFile = JavaFile.builder(packageName, type)
                        .build();
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
