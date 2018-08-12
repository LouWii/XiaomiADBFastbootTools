/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xiaomiadbfastboottools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import javafx.scene.control.TextInputControl;

/**
 *
 * @author Saki
 */
public class Uninstaller extends Command {
    
    public Uninstaller(TextInputControl control){
        super(control);
        tic.setText("");
    }
    
    public void uninstall(App app){
        pb.command(Arrays.asList((prefix + "adb shell pm uninstall --user 0 " + app.packagenameProperty().get()).split(" ")));
        pb.redirectErrorStream(false);
        output = "";
        try {
            proc = pb.start();
        } catch (IOException ex) {
            System.out.println("ERROR: Couldn't start process");
        }
        scan = new Scanner(proc.getInputStream());
        while (scan.hasNext()) {
            output += scan.nextLine() + System.lineSeparator();
        }
        tic.appendText("App: " + app.appnameProperty().get() + System.lineSeparator());
        tic.appendText("Package: " + app.packagenameProperty().get() + System.lineSeparator());
        tic.appendText("Result: " + output + System.lineSeparator());
        scan.close();
        try {
            proc.waitFor();
        } catch (InterruptedException ex) {
            System.out.println("ERROR: The process was interrupted.");
        }
    }
}
