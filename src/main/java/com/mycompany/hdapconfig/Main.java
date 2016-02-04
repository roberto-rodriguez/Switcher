/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hdapconfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author rrodriguez2
 */
public class Main {

    private static final String[] ITEMS = new String[]{"appserver", "hdapui", "static", "ui", "presence", "content"};

    private static String host = "aio1ac14.devvpc.vocal-dev.com";
//    private static String TOMCAT_INSTALLATION_PATH = "C:\\Users\\rrodriguez2\\Documents\\Install\\apache-tomcat-7.0.59\\bin\\";
//    private static String TOMCAT_CONF_PATH = "C:\\Program Files (x86)\\Apache Software Foundation\\Apache2.2\\conf\\";

    public static String CURRENT_HOST = "";

    public static final String JAVA_HOME = "JAVA_HOME";
    public static final String CATALINA_HOME = "CATALINA_HOME";
    public static final String APACHE_HOME = "APACHE_HOME";

    private static final String resourcesPath = Paths.get("").toAbsolutePath().toString().replace("target", "") + "\\resources\\";
    //+ "\\src\\main\\java\\resources\\";
    private static Map<String, String> settings = new HashMap<>();

    public static final List<String> hostList = new ArrayList<>();

    public static boolean exists(String url) {
        File f = new File(url);
        return f.exists();
    }

    public static void main(String[] args) throws Exception {
        loadSettings();
        createDomains(host);
    }

    public static void switchHost(String hostName, String host) throws IOException {
        createDomains(host);
        setEnv(host);
        setCurrentHost(hostName);
    }

    public static void createDomains(String domain) throws IOException {
        String url = settings.get(APACHE_HOME) + "\\conf\\domains.conf";

        System.out.println("url = " + url);
        File domainsFile = new File(url);

        if (domainsFile.exists()) {
            domainsFile.delete();
        }

        System.out.println("create destino: " + domainsFile.createNewFile());

        try (FileWriter fileWriter = new FileWriter(domainsFile);
                PrintWriter printWriter = new PrintWriter(fileWriter);) {
            for (String item : ITEMS) {
                printWriter.println("ProxyPass /" + item + " http://" + domain + ":8080/" + item);
                printWriter.println("ProxyPassReverse /" + item + " http://" + domain + ":8080/" + item);
            }
        }
    }

    public static void setEnv(String domain) throws IOException {
        File destino = new File(settings.get(CATALINA_HOME) + "\\bin\\setenv.bat");

        if (destino.exists()) {
            destino.delete();
        }

        System.out.println("create destino: " + destino.createNewFile());

        File envdataFile = new File(resourcesPath + "envdata.txt");

        try (FileWriter fileWriter = new FileWriter(destino);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                FileReader fileReader = new FileReader(envdataFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);) {

            printWriter.println("set " + JAVA_HOME + "=" + settings.get(JAVA_HOME));
            printWriter.println("set " + CATALINA_HOME + "=" + settings.get(CATALINA_HOME));

            printWriter.println("set DOMAIN_VARS=" + domain);
            printWriter.println(bufferedReader.readLine());
        }

    }

    public static DefaultTableModel buildTableModel() throws FileNotFoundException, IOException {
        File file = new File(resourcesPath + "\\hostdata.txt");

        DefaultTableModel tableModel;

        try (FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader);) {
            String s;

            while ((s = bufferedReader.readLine()) != null) {
                hostList.add(s);
            }

            tableModel = new DefaultTableModel(new String[]{"HOST", "URL"}, hostList.size());

            for (int i = 0; i < hostList.size(); i++) {
                tableModel.setValueAt(hostList.get(i).split(" ")[0], i, 0);
                tableModel.setValueAt(hostList.get(i).split(" ")[1], i, 1);
            }
        }
        return tableModel;
    }

    public static DefaultTableModel addHost(DefaultTableModel tableModel, String name, String value) throws FileNotFoundException, IOException {
        File hostdata = new File(resourcesPath + "\\hostdata.txt");
        try (FileWriter fileWriter = new FileWriter(hostdata); PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                printWriter.println(tableModel.getValueAt(i, 0) + " " + tableModel.getValueAt(i, 1));
            }

            printWriter.println(name + " " + value);
            tableModel.addRow(new String[]{name, value});
        }

        return tableModel;
    }

    public static DefaultTableModel editHost(DefaultTableModel tableModel, int index, String name, String value) throws FileNotFoundException, IOException {
        File hostdata = new File(resourcesPath + "\\hostdata.txt");

        try (FileWriter fileWriter = new FileWriter(hostdata); PrintWriter printWriter = new PrintWriter(fileWriter)) {
            tableModel.setValueAt(name, index, 0);
            tableModel.setValueAt(value, index, 1);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                printWriter.println(tableModel.getValueAt(i, 0) + " " + tableModel.getValueAt(i, 1));
            }
        }

        return tableModel;
    }

    public static DefaultTableModel deleteHost(DefaultTableModel tableModel, int index) throws FileNotFoundException, IOException {
        File hostdata = new File(resourcesPath + "\\hostdata.txt");

        try (FileWriter fileWriter = new FileWriter(hostdata); PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (i != index) {
                    printWriter.println(tableModel.getValueAt(i, 0) + " " + tableModel.getValueAt(i, 1));
                }
            }
            tableModel.removeRow(index);
        }

        return tableModel;
    }

    public static String loadCurrentHost() throws FileNotFoundException, IOException {
        File currentHostFile = new File(resourcesPath + "current_host.txt");
        try (FileReader fileReader = new FileReader(currentHostFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);) {
            CURRENT_HOST = bufferedReader.readLine();
        }
        return CURRENT_HOST;
    }

    public static void setCurrentHost(String currentHost) throws FileNotFoundException, IOException {
        File currentHostFile = new File(resourcesPath + "current_host.txt");
        try (FileWriter fileWriter = new FileWriter(currentHostFile);
                PrintWriter printWriter = new PrintWriter(fileWriter);) {
            CURRENT_HOST = currentHost;
            printWriter.println(currentHost);
        }
    }

    public static String getCURRENT_HOST() {
        return CURRENT_HOST;
    }

    public static Map<String, String> getSettings() {
        return settings;
    }

    public static boolean loadSettings() throws Exception {
        File settingsFile = new File(resourcesPath + "settings.txt");

        try (FileReader fileReader = new FileReader(settingsFile); BufferedReader bufferedReader = new BufferedReader(fileReader);) {
            try {
                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    settings.put(s.split("=")[0], s.split("=")[1]);
                }
            } catch (Exception e) {
                throw new Exception("Error loading settings. Chack the file settings.txt in resources.");
            }
        }
        return settings.containsKey(JAVA_HOME) && exists(settings.get(JAVA_HOME))
                && settings.containsKey(CATALINA_HOME) && exists(settings.get(CATALINA_HOME))
                && settings.containsKey(APACHE_HOME) && exists(settings.get(APACHE_HOME));
    }

    public static void saveSettings(Map<String, String> newSettings) throws Exception {
        File settingsFile = new File(resourcesPath + "settings.txt");

        try (FileWriter fileWriter = new FileWriter(settingsFile);PrintWriter printWriter = new PrintWriter(fileWriter);) {
            for (String key : newSettings.keySet()) {
               printWriter.println(key + "=" + newSettings.get(key));
            }
            
            settings = newSettings;
        }
    }
}
