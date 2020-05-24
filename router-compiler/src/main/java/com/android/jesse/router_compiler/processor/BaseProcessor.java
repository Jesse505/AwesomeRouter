package com.android.jesse.router_compiler.processor;

import com.android.jesse.router_compiler.utils.Consts;
import com.android.jesse.router_compiler.utils.Logger;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


public abstract class BaseProcessor extends AbstractProcessor {

    // 操作Element工具类 (类、函数、属性都是Element)
    Elements elementUtils;

    // type(类信息)工具类，包含用于操作TypeMirror的工具方法
    Types typeUtils;

    // 文件生成器 类/资源，Filter用来创建新的类文件，class文件以及辅助文件
    Filer filer;

    Logger logger;

    /**
     * 支持的java版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 支持的参数选项
     */
    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<String>() {{
            this.add(Consts.KEY_MODULE_NAME);
        }};
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filer = processingEnvironment.getFiler();
        logger = new Logger(processingEnvironment.getMessager());

        String name = processingEnvironment.getOptions().get(Consts.KEY_MODULE_NAME);
        logger.info("模块的名称：" + name);
    }

}
