package com.ai.runner.gradle.plugin.jar;

import java.io.File;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class APIJarProcessTask extends DefaultTask {

    /**
     * 需要解压的jar路径
     */
    private List<File> jarPaths;

    /**
     * 解压后的目标路径
     */
    private File destinationDir;

    @Input
    public File getDestinationDir() {
        return destinationDir;
    }

    @Input
    public List<File> getJarPaths() {
        return jarPaths;
    }

    public void setJarPaths(List<File> jarPaths) {
        this.jarPaths = jarPaths;
    }

    public void setDestinationDir(File destinationDir) {
        this.destinationDir = destinationDir;
    }

    @TaskAction
    public void unJar() throws Exception {
        System.out.println("begin to decompress all jars,size="+jarPaths.size());
        validateParam();
        if(!destinationDir.exists()){
            destinationDir = new File(destinationDir.getAbsolutePath());
            System.out.println("destination dir is not exists,make it!");
        }
        System.out.println("destination dir is:"+destinationDir.getAbsolutePath());
        for (File jarFile : jarPaths) {
        	//gucl 20161122
        	//只解压sources.jar的jar包，即只解压源码jar
        	if(jarFile.getAbsolutePath().endsWith("sources.jar")){
        		JarUtil.unJar(jarFile, destinationDir);
        		System.out.println("decompressed successful for jar:"+jarFile.getAbsolutePath());
        	}
        }
        System.out.println("decompressed all jars successful");
    }

    private void validateParam() {
        if (jarPaths == null) {
            throw new IllegalArgumentException("jarPaths can not be empty");
        }
        if (destinationDir == null)
            throw new IllegalArgumentException("destinationDir can not be empty");
        if(destinationDir.isFile()){
            throw new IllegalArgumentException("destinationDir is a file but not dir");
        }
    }

}
