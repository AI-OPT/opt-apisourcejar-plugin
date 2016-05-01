package com.ai.runner.gradle.plugin.jar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class JarUtil {

    public static void jar(File desJar, File jarDir) throws Exception {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(desJar));
        File[] src = jarDir.listFiles();
        jar(out, src);
    }

    public static void jar(OutputStream out, File[] src) throws Exception {
        jar(out, src, null, null);
    }

    public static void jar(OutputStream out, File src) throws Exception {
        jar(out, new File[] { src }, null, null);
    }

    public static void jar(OutputStream out, File[] src, String prefix, Manifest man)
            throws Exception {
        JarOutputStream jout = null;
        if (man == null) {
            jout = new JarOutputStream(out);
        } else {
            jout = new JarOutputStream(out, man);
        }

        if (prefix != null && prefix.trim().length() > 0 && !prefix.equals("/")) {
            if (prefix.charAt(0) == '/') {
                prefix = prefix.substring(1);
            }
            if (prefix.charAt(prefix.length() - 1) != '/') {
                prefix = prefix + "/";
            }
        } else {
            prefix = "";
        }
        for (File f : src) {
            jar(f, prefix, jout);
        }
        jout.close();
    }

    private static void jar(File src, String prefix, JarOutputStream jout) throws Exception {
        if (src.isDirectory()) {
            prefix = prefix + src.getName() + "/";
            ZipEntry entry = new ZipEntry(prefix);
            entry.setTime(src.lastModified());
            entry.setMethod(JarOutputStream.STORED);
            entry.setSize(0l);
            entry.setCrc(0l);
            jout.putNextEntry(entry);
            jout.closeEntry();
            File[] files = src.listFiles();
            if (files != null) {
                for (File file : files) {
                    jar(file, prefix, jout);
                }
            }
        } else {
            byte[] buffer = new byte[8092];
            ZipEntry entry = new ZipEntry(prefix + src.getName());
            entry.setTime(src.lastModified());
            jout.putNextEntry(entry);
            FileInputStream in = new FileInputStream(src);
            int len;
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                jout.write(buffer, 0, len);
            }
            in.close();
            jout.closeEntry();
        }
    }

    public static void unJar(File jarFile, File unJarDir) throws Exception {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(jarFile));
        unJar(in, unJarDir);

    }

    public static void unJar(InputStream in, File unJarDir) throws Exception {
        if (!unJarDir.exists()) {
            unJarDir.mkdirs();
        }

        JarInputStream jin = new JarInputStream(in);
        byte[] buffer = new byte[8092];

        ZipEntry entry = jin.getNextEntry();
        while (entry != null) {
            String fileName = entry.getName();
            if (File.separatorChar != '/') {
                fileName = fileName.replace('/', File.separatorChar);
            }
            if (fileName.charAt(fileName.length() - 1) == '/') {
                fileName = fileName.substring(0, fileName.length() - 1);
            }
            if (fileName.charAt(0) == '/') {
                fileName = fileName.substring(1);
            }
            File file = new File(unJarDir, fileName);
            if (entry.isDirectory()) {
                file.mkdirs();
            } else {
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                int len = 0;
                while ((len = jin.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                file.setLastModified(entry.getTime());
            }
            jin.closeEntry();
            entry = jin.getNextEntry();
        }

        Manifest mf = jin.getManifest();
        if (mf != null) {
            File file = new File(unJarDir, "META-INF/MANIFEST.MF");
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            OutputStream out = new FileOutputStream(file);
            mf.write(out);
            out.flush();
            out.close();
        }

        jin.close();
    }

    public static void main(String[] args) throws Exception {
        File jarF = new File(
                "/Users/zhangchao/Downloads/runner-base-1.0-20151106.140125-1-sources.jar");
       // File unJarDir = new File("/Users/zhangchao/Downloads/a");
        //unJar(jarF, unJarDir);

        System.out.println(jarF.getAbsolutePath());
    }

}
