package com.dong.library.reader.plugin

//import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class KReaderPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("router", KReaderExtension)

        def kaptExtension = project.extensions.getByName('kapt')
        def androidExtension = project.extensions.getByName("android")
        if (kaptExtension != null) {
            kaptExtension.arguments {
                arg("moduleName", project.getName())
            }
        }

        project.afterEvaluate {
//            if (project.router.autoAddDependency) {
//                project.configurations.all { configuration ->
//                    def name = configuration.name
//                    if (name == "implementation" || name == "compile") {
//                        System.out.println("Add krouter-api dependency")
//                        configuration.dependencies.add(project.dependencies.create(Const.KROUTER_API))
//                    }
//                }
//            }
//            if (!project.plugins.hasPlugin(AndroidBasePlugin.class)) {
//                return
//            }
            def assetPath = project.path + "/assets"
            for (dir in androidExtension.sourceSets.main.assets.getSrcDirs()) {
                if (dir.path != null && dir.path.length() > 0) {
                    assetPath = dir.path
                }
            }

            def file = new File(assetPath + "/KReader_" + project.name)
//            def file = new File(assetPath + "/${UtilsKt.PROJECT_NAME}${UtilsKt.SEPARATOR}" + project.name)
            file.parentFile.mkdirs()
            file.createNewFile()
        }

    }
}