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
                    System.out.println("Add reader-processors:${Const.V_PROCESSOR} dependency")
                    configuration.dependencies.add(project.dependencies.create(Const.KREADER_PROCESSOR))
                }
            }
        }

        project.afterEvaluate {
            if (project.reader.autoAddDependency) {
                project.configurations.all { configuration ->
                    def name = configuration.name
                    if (name == "implementation" || name == "compile") {
                        System.out.println("Add reader-api:${Const.V_API} dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_API))
                        System.out.println("Add reader-annotations:${Const.V_ANNOTATION} dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_ANNOTATION))
                        System.out.println("Add okhttp:${Const.V_OKHTTP} dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_OKHTTP))
                        System.out.println("Add retrofit:${Const.V_RETROFIT} dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_RETROFIT))
                        System.out.println("Add rconverter-scalars:${Const.V_SCALARS} dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_SCALARS))
                        System.out.println("Add rconverter-gson:${Const.V_GSON} dependency")
                        configuration.dependencies.add(project.dependencies.create(Const.KREADER_GSON))
                        System.out.println("Add anko-sdk15:${Const.V_ANKO} dependency")
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