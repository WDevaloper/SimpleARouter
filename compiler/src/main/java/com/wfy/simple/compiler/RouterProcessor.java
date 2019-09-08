package com.wfy.simple.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.wfy.simple.library.Route;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.wfy.simple.compiler.Constants.GENERATE_ROUTE_SUFFIX_NAME;
import static com.wfy.simple.compiler.Constants.GENERATE_ROUTE_TABLE_PACKAGE;
import static com.wfy.simple.compiler.Constants.MODULE_NAME;
import static com.wfy.simple.compiler.Constants.SUPER_INTERFACE;

public class RouterProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Map<String, ClassName> routes = new HashMap<>();
    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        Map<String, String> options = processingEnvironment.getOptions();
        moduleName = options.get("moduleName");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        parseRoute(roundEnvironment);
        parseRouteFile();
        return true;
    }

    private void parseRoute(RoundEnvironment roundEnvironment) {
        for (Element e : roundEnvironment.getElementsAnnotatedWith(Route.class)) {
            Route route = e.getAnnotation(Route.class);
            String path = route.path();
            TypeElement te = (TypeElement) e;
            ClassName className = ClassName.get(te);
            routes.put(path, className);
        }
    }


    private void parseRouteFile() {
        //build type
        TypeSpec.Builder builder =
                TypeSpec.classBuilder(GENERATE_ROUTE_SUFFIX_NAME + moduleName)
                        .addModifiers(Modifier.PUBLIC);


        TypeName superInterface = ClassName.bestGuess(SUPER_INTERFACE);
        builder.addSuperinterface(superInterface);

        //build Map<String, Class<?>>
        ParameterizedTypeName mapType = ParameterizedTypeName.get(
                //Map
                ClassName.get(Map.class),
                //string
                ClassName.get(String.class),
                //Class<?>
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(Object.class)
                )
        );

        //build method for loadInto(Map<String, Class<?>> routes)
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("loadInto")
                .addAnnotation(Override.class)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapType, "routes");


        //build method body
        for (String key : routes.keySet()) {
            methodBuilder.addStatement("routes.put($S,$T.class)", key, routes.get(key));
        }
        builder.addMethod(methodBuilder.build());

        try {
            JavaFile javaFile = JavaFile.builder(GENERATE_ROUTE_TABLE_PACKAGE, builder.build()).build();
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Route.class.getCanonicalName());
        return annotations;
    }


    @Override
    public Set<String> getSupportedOptions() {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add(MODULE_NAME);
        return hashSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
