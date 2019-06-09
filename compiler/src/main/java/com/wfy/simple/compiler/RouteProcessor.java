package com.wfy.simple.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wfy.simple.library.Route;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class RouteProcessor extends AbstractProcessor {
    private static final String PREFIX = "RouterMapTab_";

    private String moduleName;


    private String modulePackageName = "";

    private String superInterfaceSimpleName = "IRoute";
    private String superInterfacePackage = "com.wfy.simple.library";
    private String superInterfaceName = superInterfacePackage + "." + superInterfaceSimpleName;
    private String superInterfaceMeyhodName = "loadInto";


    private Filer filer;
    private Map<String, String> routes = new HashMap<>();
    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element e : roundEnvironment.getElementsAnnotatedWith(Route.class)) {
            addRoute(e);
        }
        createRouteFile();

        return true;
    }

    private void createRouteFile() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(PREFIX + moduleName).addModifiers(Modifier.PUBLIC);
        TypeName superInterface = ClassName.bestGuess(superInterfaceName);
        builder.addSuperinterface(superInterface);

        //Map<String,String>
        TypeName stringType = ClassName.get(String.class);

        TypeName mapType = ParameterizedTypeName.get(ClassName.get(Map.class), stringType, stringType);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(superInterfaceMeyhodName)
                .addAnnotation(Override.class)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapType, "routes");

        for (String key : routes.keySet()) {
            methodBuilder.addStatement("routes.put($S,$S)", key, routes.get(key));
        }
        builder.addMethod(methodBuilder.build());
        JavaFile javaFile = JavaFile.builder(modulePackageName, builder.build()).build();//将源码输出到ARouter.ROUTES_PACKAGE_NAME,
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
        }
    }


    private void addRoute(Element e) {
        Route route = e.getAnnotation(Route.class);
        String path = route.path();
        String name = e.toString();
        modulePackageName = getModulePackageName(e);
        moduleName = path.substring(1, path.lastIndexOf("/"));
        routes.put(path, name);

        messager.printMessage(Diagnostic.Kind.NOTE, "======准备在gradle的控制台打印信息========== " + path);

    }


    private String getModulePackageName(Element e) {
        Element enclosingElement = e.getEnclosingElement();
        if (enclosingElement instanceof PackageElement) {
            return ((PackageElement) enclosingElement).getQualifiedName().toString();
        }
        return "";
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Route.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
