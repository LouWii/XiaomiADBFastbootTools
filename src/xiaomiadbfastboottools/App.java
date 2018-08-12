/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xiaomiadbfastboottools;

import javafx.beans.property.*;

/**
 *
 * @author Saki
 */
public class App {
    private StringProperty appname = new SimpleStringProperty();
    private StringProperty packagename = new SimpleStringProperty();
    private BooleanProperty selected = new SimpleBooleanProperty();

    public App(String a, String b) {
        appname.set(a);
        packagename.set(b);
        selected.set(false);
    }
    
    public StringProperty appnameProperty(){
        return appname;
    }
    public StringProperty packagenameProperty(){
        return packagename;
    }
    public BooleanProperty selectedProperty(){
        return selected;
    }
}
