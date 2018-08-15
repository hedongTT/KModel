package com.dong.library.reader.plugin

import com.android.build.gradle.api.AndroidBasePlugin
import com.dong.library.reader.annotations.UtilsKt
import org.gradle.api.Plugin
import org.gradle.api.Project

class KReaderPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("reader", KReaderExtension)

        def kaptExtension = project.extensions.getByName('kapt')
        def androidExtension = project.extensions.getByName("android")
        if (kaptExtension != null) {
            kaptExtension.arguments {
                arg("moduleName", project.getName())
            }
        }

        if (project.reader.autoAddDependency) {
            project.configurations.all { configuration ->
                def name = configuration.name
                if (name == "kapt") {
                    System.out.println("Add reader-processors:1.0.1 dependency")
                    configuration.dependencies.add(project.dependencies.create(Const.KREADER_PROCESSOR))
                }
            }
        }

        project.afterEvaluate {
            if (project.reader.autoAddDependency) {
                project.configurations.all { configuration ->
                    def name = configuration.name
                    if (name == "implementation" || name == "compile") {
                        System.out.println("Add reader-api:1.0.0 dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_API))
                        System.out.println("Add reader-annotations:1.0.1 dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_ANNOTATION))
                        System.out.println("Add okhttp:3.9.1 dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_OKHTTP))
                        System.out.println("Add retrofit:2.1.0 dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_RETROFIT))
                        System.out.println("Add rconverter-scalars:2.0.2 dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_SCALARS))
                        System.out.println("Add anko-sdk15:0.9.1 dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_ANKO))
                    }
                }
            }
            if (!project.plugins.hasPlugin(AndroidBasePlugin.class)) {
                return
            }
            def assetPath = project.path + "/assets"
            for (dir in androidExtension.sourceSets.main.assets.getSrcDirs()) {
                if (dir.path != null && dir.path.length() > 0) {
                    assetPath = dir.path
                }
            }
            def file = new File(assetPath + "/${UtilsKt.PROJECT_NAME}${UtilsKt.SEPARATOR}" + project.name)
            file.parentFile.mkdirs()
            file.createNewFile()
        }
    }
}