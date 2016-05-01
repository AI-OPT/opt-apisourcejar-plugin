package com.ai.runner.gradle.plugin.jar;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class APIJarProcessPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        target.getTasks().create("APIJarProcess", APIJarProcessTask.class);
    }

}
