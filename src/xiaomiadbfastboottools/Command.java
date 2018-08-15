/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xiaomiadbfastboottools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import javafx.scene.control.TextInputControl;

/**
 *
 * @author Saki
 */
public class Command {
    protected ProcessBuilder pb;
    protected Process proc;
    protected Scanner scan;
    protected String output;
    protected TextInputControl tic;
    protected String prefix;
    
    public Command(){
        pb = new ProcessBuilder();
        pb.directory(new File(System.getProperty("user.dir") + "/temp"));
        tic = null;
        if (System.getProperty("os.name").toLowerCase().contains("linux"))
        	prefix = "";
        else if (System.getProperty("os.name").toLowerCase().contains("mac os"))
        	prefix = "./";
        else prefix = "temp/";
    }
    
    public Command(TextInputControl control){
        pb = new ProcessBuilder();
        pb.directory(new File(System.getProperty("user.dir") + "/temp"));
        tic = control;
        if (System.getProperty("os.name").toLowerCase().contains("linux"))
        	prefix = "";
        else if (System.getProperty("os.name").toLowerCase().contains("mac os"))
        	prefix = "./";
        else prefix = "temp/";
    }
    
    public String exec(String arg){
        pb.command(Arrays.asList((prefix + arg).split(" ")));
        pb.redirectErrorStream(true);
        output = "";
        if (tic != null)
            tic.setText("");
        try {
            proc = pb.start();
        } catch (IOException ex) {
            System.out.println("ERROR: Couldn't start process");
            System.out.println(ex.getMessage());
        }
        scan = new Scanner(proc.getInputStream());
        String line;
        while (scan.hasNext()){
            line = scan.nextLine() + System.lineSeparator();
            output += line;
            if (tic != null)
             tic.appendText(line);
        }
        scan.close();
        return output;
    }
    
    public String exec(String arg, boolean err){
        pb.command(Arrays.asList((prefix + arg).split(" ")));
        pb.redirectErrorStream(false);
        output = "";
        if (tic != null)
            tic.setText("");
        try {
            proc = pb.start();
        } catch (IOException ex) {
            System.out.println("ERROR: Couldn't start process");
            System.out.println(ex.getMessage());
        }
        if (err)
            scan = new Scanner(proc.getErrorStream());
                    else 
            scan = new Scanner(proc.getInputStream());
        String line;
        while (scan.hasNext()) {
            line = scan.nextLine() + System.lineSeparator();
            output += line;
            if (tic != null) {
                tic.appendText(line);
            }
        }
        scan.close();
        return output;
    }
    
    public String exec(String... args){
        pb.redirectErrorStream(true);
        output = "";
        if (tic != null)
            tic.setText("");
        for (String s : args){
            pb.command(Arrays.asList((prefix + s).split(" ")));
            try {
                proc = pb.start();
            } catch (IOException ex) {
                System.out.println("ERROR: Couldn't start process");
            }
            scan = new Scanner(proc.getInputStream());
            String line;
            while (scan.hasNext()) {
                line = scan.nextLine() + System.lineSeparator();
                output += line;
                if (tic != null)
                    tic.appendText(line);
            }
            scan.close();
            try {
                proc.waitFor();
            } catch (InterruptedException ex) {
                System.out.println("ERROR: The process was interrupted.");
            }
        }
        return output;
    }
    
    public void waitFor(){
        try {
            proc.waitFor();
        } catch (InterruptedException ex) {
            System.out.println("ERROR: The process was interrupted.");
        }
    }
    
    public void kill(){
        proc.destroy();
        if (proc.isAlive())
            proc.destroyForcibly();
    }
}
