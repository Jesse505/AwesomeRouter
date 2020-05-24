package com.android.jesse.router_compiler.processor;

import com.android.jesse.router_annotation.Router;
import com.android.jesse.router_annotation.model.RouteMeta;
import com.android.jesse.router_compiler.utils.Consts;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
@SupportedAnnotationTypes(Consts.ANNOTATION_TYPE_ROUTER)
public class RouterProcessor extends BaseProcessor {

    private Map<String, List<RouteMeta>> tempPathMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 一旦有类之上使用@Router注解
        if (CollectionUtils.isNotEmpty(set)) {
            // 获取所有被 @Router 注解的 元素集合
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Router.class);

            if (CollectionUtils.isNotEmpty(elements)) {
                // 解析元素
                try {
                    parseElements(elements);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 返回true表示处理@Router注解完成
            return true;
        }
        return false;

    }

    private void parseElements(Set<? extends Element> elements) throws IOException {

        // 通过Element工具类，获取Activity、Callback类型
        TypeElement activityType = elementUtils.getTypeElement(Consts.ACTIVITY);

        // 显示类信息（获取被注解节点，类节点）这里也叫自描述 Mirror
        TypeMirror activityMirror = activityType.asType();

        // 遍历节点
        for (Element element : elements) {
            // 获取每个元素类信息，用于比较
            TypeMirror elementMirror = element.asType();

            // 获取每个类上的@ARouter注解中的注解值
            Router router = element.getAnnotation(Router.class);

            // 路由详细信息，最终实体封装类
            RouteMeta bean = new RouteMeta.Builder()
                    .setGroup(router.group())
                    .setPath(router.path())
                    .setElement(element)
                    .build();

            // 高级判断：ARouter注解仅能用在类之上，并且是规定的Activity
            // 类型工具类方法isSubtype，相当于instance一样
            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                bean.setType(RouteMeta.Type.ACTIVITY);
            } else {
                // 不匹配抛出异常，这里谨慎使用！考虑维护问题
                throw new RuntimeException("@ARouter注解目前仅限用于Activity类之上");
            }

            // 赋值临时map存储，用来存放路由组Group对应的详细Path类对象集合
            valueOfPathMap(bean);
        }

        // (生成类文件需要实现的接口）
        TypeElement groupType = elementUtils.getTypeElement(Consts.AROUTE_GROUP); // 路径接口

        createGroupFile(groupType);
    }

    /**
     * 生成路由组Group类文件，如：Router$$Group$$app
     * public class Router$$Group$$app implements IRouteGroup {
     *
     *  @Override
     *  public void loadInto(Map<String, RouteMeta> atlas) {
     *      atlas.put("/app/MainActivity", RouteMeta.build(RouteMeta.Type.ACTIVITY, MainActivity.class, "/app/MainActivity", "app"));
     *  }
     * }
     */
    private void createGroupFile(TypeElement groupType) throws IOException {
        // 判断是否有需要生成的类文件
        if (MapUtils.isEmpty(tempPathMap)) return;

        //返回值类型
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class), // Map
                ClassName.get(String.class), // Map<String,
                ClassName.get(RouteMeta.class) // Map<String, RouteMeta>
        );

        // 遍历分组，每一个分组创建一个路径类文件，如：ARouter$$Path$$app
        for (Map.Entry<String, List<RouteMeta>> entry : tempPathMap.entrySet()) {
            // 方法配置：public Map<String, RouteMeta> loadPath() {
            MethodSpec.Builder methodBuidler = MethodSpec.methodBuilder(Consts.METHOD_LOAD_PATH) // 方法名
                    .addAnnotation(Override.class) // 重写注解
                    .addModifiers(Modifier.PUBLIC) // public修饰符
                    .returns(methodReturns); // 返回值类型

            // 遍历之前：Map<String, RouterBean> pathMap = new HashMap<>();
            methodBuidler.addStatement("$T<$T, $T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteMeta.class),
                    Consts.PATH_PARAMETER_NAME,
                    HashMap.class);

            List<RouteMeta> pathList = entry.getValue();
            // 方法内容配置（遍历每个分组中每个路由详细路径）
            for (RouteMeta bean : pathList) {
                // pathMap.put("/app/MainActivity", RouteMeta.build(
                //        RouteMeta.Type.ACTIVITY, MainActivity.class, "/app/MainActivity", "app"));
                methodBuidler.addStatement(
                        "$N.put($S, $T.build($T.$L, $T.class, $S, $S))",
                        Consts.PATH_PARAMETER_NAME,
                        bean.getPath(), // "/app/MainActivity"
                        ClassName.get(RouteMeta.class), // RouteMeta
                        ClassName.get(RouteMeta.Type.class), // RouteMeta.Type
                        bean.getType(), // 枚举类型：ACTIVITY
                        ClassName.get((TypeElement) bean.getElement()), // MainActivity.class
                        bean.getPath(), // 路径名
                        bean.getGroup() // 组名
                );
            }

            // 遍历之后：return pathMap;
            methodBuidler.addStatement("return $N", Consts.PATH_PARAMETER_NAME);

            // 最终生成的类文件名
            String finalClassName = Consts.GROUP_FILE_NAME + entry.getKey();
            logger.info("APT生成路由Group类文件：" +
                    Consts.PACKAGE_OF_GENERATE_FILE + "." + finalClassName);

            // 生成类文件：Router$$Group$$app
            JavaFile.builder(Consts.PACKAGE_OF_GENERATE_FILE, // 包名
                    TypeSpec.classBuilder(finalClassName) // 类名
                            .addSuperinterface(ClassName.get(groupType)) // 实现IRouteGroup接口
                            .addModifiers(Modifier.PUBLIC) // public修饰符
                            .addMethod(methodBuidler.build()) // 方法的构建（方法参数 + 方法体）
                            .build()) // 类构建完成
                    .build() // JavaFile构建完成
                    .writeTo(filer); // 文件生成器开始生成类文件

        }
    }

    private void valueOfPathMap(RouteMeta bean) {

        if (checkRouterPath(bean)) {
            logger.info("RouterBean >>> " + bean.toString());

            // 开始赋值Map
            List<RouteMeta> routeMetas = tempPathMap.get(bean.getGroup());
            // 如果从Map中找不到key为：bean.getGroup()的数据，就新建List集合再添加进Map
            if (CollectionUtils.isEmpty(routeMetas)) {
                routeMetas = new ArrayList<>();
                routeMetas.add(bean);
                tempPathMap.put(bean.getGroup(), routeMetas);
            } else { // 找到了key，直接加入List集合
                routeMetas.add(bean);
            }

        } else {
            logger.error("@Router注解未按规范配置，如：/app/MainActivity");
        }
    }

    private boolean checkRouterPath(RouteMeta bean) {
        String group = bean.getGroup();
        String path = bean.getPath();
        // @Router注解中的path值，必须要以 / 开头（模仿阿里Arouter规范）
        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {
            logger.error("@Router注解中的path值，必须要以 / 开头");
            return false;
        }
        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            // 架构师定义规范，让开发者遵循
            logger.error("@ARouter注解未按规范配置，如：/app/MainActivity");
            return false;
        }
        // @Router注解中的group有赋值情况
        if (StringUtils.isNotEmpty(group)) {
            bean.setGroup(group);
        } else {
            // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app 作为group
            String finalGroup = path.substring(1, path.indexOf("/", 1));
            bean.setGroup(finalGroup);
        }
        return true;
    }
}
