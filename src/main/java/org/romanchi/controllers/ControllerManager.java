package org.romanchi.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.romanchi.Main;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Роман on 28.01.2017.
 */
public class ControllerManager {
    private static ControllerManager ourInstance = new ControllerManager();
    private static HashMap<String,Ctrl> controllers = new HashMap<String,Ctrl>();
    private static HashMap<String,Scene> scenes = new HashMap<String, Scene>();
    public static void addController(String key, Ctrl controller){
        if(controllers.get(key)==null){
            controllers.put(key,controller);
        }
    }
    public static void addScene(String key, Scene scene){
        if(scenes.get(key)==null){
            scenes.put(key,scene);
        }
    }
    public static HashMap<String, Scene> getScenes(){
        return scenes;
    }
    public static HashMap<String,Ctrl> getControllers() {
        return controllers;
    }

    public static ControllerManager getInstance() {
        return ourInstance;
    }
    public static Scene changeSceneTo(String controllerName, String viewName){
        if(!ControllerManager.getControllers().containsKey(controllerName)){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/view/" + viewName + ".fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ControllerManager.getControllers().put(controllerName, (Ctrl) loader.getController());
            Scene scene = new Scene(root);
            ControllerManager.getScenes().put(controllerName, scene);
        }
        return ControllerManager.getScenes().get(controllerName);
    }
    private ControllerManager() {

    }
}