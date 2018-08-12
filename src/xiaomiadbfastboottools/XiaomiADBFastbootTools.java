/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xiaomiadbfastboottools;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Saki
 */
public class XiaomiADBFastbootTools extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Xiaomi ADB/Fastboot Tools");
        stage.getIcons().add(new Image(this.getClass().getClassLoader().getResource("icon.png").toString()));
        stage.show();
        stage.setResizable(false);
    }
    
    @Override
    public void stop(){
        Command comm = new Command();
        comm.exec("adb kill-server");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            System.out.println("ERROR: Couldn't sleep!");
        }
        try {
            FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + "/temp"));
        } catch (IOException ex) {
            System.out.println("ERROR: Couldn't delete directory!");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
