package xiaomiadbfastboottools;

import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;

import org.apache.commons.io.IOUtils;

public class MainWindowController implements Initializable {
    
    @FXML
    private Menu optionsMenu;
    @FXML
    private MenuItem checkMenuItem;
    @FXML
    private Menu rebootMenu;
    @FXML
    private MenuItem systemMenuItem;
    @FXML
    private MenuItem recoveryMenuItem;
    @FXML
    private MenuItem fastbootMenuItem;
    @FXML
    private MenuItem edlMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private Label serialLabel;
    @FXML
    private Label codenameLabel;
    @FXML
    private Label bootloaderLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TableView<App> debloaterTableView;
    @FXML
    private TableColumn<App, Boolean> checkTableColumn;
    @FXML
    private TableColumn<App, String> appTableColumn;
    @FXML
    private TableColumn<App, String> packageTableColumn;
    @FXML
    private TextField customappTextField;
    @FXML
    private Button uninstallButton;
    @FXML
    private Button addButton;
    @FXML
    private Button reboottwrpButton;
    @FXML
    private Button disableButton;
    @FXML
    private Button enableButton;
    @FXML
    private Button disableEISButton;
    @FXML
    private Button enableEISButton;
    @FXML
    private Button readpropertiesButton;
    @FXML
    private Button savepropertiesButton;
    @FXML
    private Button antirbButton;
    @FXML
    private Button browseimageButton;
    @FXML
    private ComboBox<String> partitionComboBox;
    @FXML
    private Button flashButton;
    @FXML
    private CheckBox autobootCheckBox;
    @FXML
    private Label imageLabel;
    @FXML
    private Button bootButton;
    @FXML
    private Button cacheButton;
    @FXML
    private Button cachedataButton;
    @FXML
    private Button unlockButton;
    @FXML
    private Button lockButton;
    @FXML
    private Tab adbTab;
    @FXML
    private Tab fastbootTab;
    
    String image;
    Command comm;
    
    public void setupWidgets(){
        serialLabel.setText("-");
        codenameLabel.setText("-");
        bootloaderLabel.setText("-");
        partitionComboBox.getItems().addAll(
        "boot","cust","modem","persist","recovery","system");
        image = "";
    }
    
    public void setLabels(String serial, String codename, String bl){
        serialLabel.setText(serial);
        codenameLabel.setText(codename);
        bootloaderLabel.setText(bl);
    }
    
    public void setADB(boolean adb){
        adbTab.setDisable(!adb);
        recoveryMenuItem.setDisable(!adb);
        if (adb){
            outputTextArea.setText("Device found!");
            rebootMenu.setDisable(false);
            fastbootTab.setDisable(true);
        } else {
            rebootMenu.setDisable(true);
            outputTextArea.setText("No device found!");
            setLabels("-", "-", "-");
        }
    }
    
    public void setFastboot(boolean fastboot){
        recoveryMenuItem.setDisable(fastboot);
        fastbootTab.setDisable(!fastboot);
        if (fastboot){
            outputTextArea.setText("Device found!");
            rebootMenu.setDisable(false);
            adbTab.setDisable(true);
        } else {
            rebootMenu.setDisable(true);
            outputTextArea.setText("No device found!");
            setLabels("-", "-", "-");
        }
    }
    
    public void createFile(String file){
    	createFile(file, false);
    }
    
    public void createFile(String file, Boolean setExecPermission){
        File temp = new File(System.getProperty("user.dir") + "/temp");
        temp.mkdir();
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(file));
        } catch (IOException ex) {
            System.out.println("ERROR: Couldn't read resource.");
        }
        if (file.lastIndexOf("/") != -1)
            file = file.substring(file.lastIndexOf("/")+1);
        File newfile = new File(System.getProperty("user.dir") + "/temp/" + file);
        if (!newfile.exists()) {
            try {
                newfile.createNewFile();
                FileOutputStream fos = new FileOutputStream(newfile);
                fos.write(bytes);
                fos.flush();
                fos.close();

                if (setExecPermission) {
                	newfile = new File(System.getProperty("user.dir") + "/temp/" + file);
                	newfile.setExecutable(true);
                }
            } catch (IOException ex) {
                System.out.println("ERROR: Couldn't create file.");
            }
        }
    }
    
    public void setupFiles(){
        String os = System.getProperty("os.name").toLowerCase();
        createFile("dummy.img");
        if (os.contains("win")){
            createFile("windows/adb.exe");
            createFile("windows/fastboot.exe");
            createFile("windows/AdbWinApi.dll");
            createFile("windows/AdbWinUsbApi.dll");
        }
        if (os.contains("mac")){
            createFile("macos/adb", true);
            createFile("macos/fastboot", true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupWidgets();
        setupFiles();
        comm = new Command();
        comm.exec("adb start-server");
    }
    
    public boolean checkFastboot(){
        comm = new Command();
        String op = comm.exec("fastboot devices", false);
        if (op.length() < 1) {
            setFastboot(false);
            return false;
        }
        String codename = comm.exec("fastboot getvar product", true);
        setFastboot(true);
        setLabels(op.substring(0, op.indexOf("fa")).trim(), codename.substring(9, codename.indexOf(System.lineSeparator())).trim(), "-");
        op = comm.exec("fastboot oem device-info", true);
        if (op.contains("unlocked: true")) {
            bootloaderLabel.setText("unlocked");
        }
        if (op.contains("unlocked: false")) {
            bootloaderLabel.setText("locked");
        }
        return true;
    }
    
    public boolean checkADB(){
        comm = new Command();
        String op = comm.exec("adb get-serialno", true);
        if (op.contains("no devices")) {
            setADB(false);
            return false;
        }
        if (op.contains("unauthorized")) {
            setFastboot(false);
            outputTextArea.setText("Device unauthorised!\nPlease allow USB debugging!");
            return false;
        }
        setADB(true);
        setLabels(comm.exec("adb get-serialno", false).trim(), comm.exec("adb shell getprop ro.product.name", false).trim(), "-");
        op = comm.exec("adb shell getprop ro.boot.flash.locked", false);
        if (op.contains("0")) {
            bootloaderLabel.setText("unlocked");
        }
        if (op.contains("1")) {
            bootloaderLabel.setText("locked");
        }
        return true;
    }
    
    public boolean checkDevice(){
        if (!checkFastboot()){
            if(checkADB()){
                createTable();
                return true;
            }
            return false;
        }
        return true;
    }
    
    public boolean checkcamera2(){
        comm = new Command();
        return comm.exec("adb shell getprop persist.camera.HAL3.enabled").contains("1");
    }
    
    public boolean checkEIS(){
        comm = new Command();
        return comm.exec("adb shell getprop persist.camera.eis.enable").contains("1");
    }
    
    @SuppressWarnings("unchecked")
	public void createTable(){
        debloaterTableView.setItems(getApps());

        checkTableColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        checkTableColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
        appTableColumn.setCellValueFactory(new PropertyValueFactory<>("appname"));
        packageTableColumn.setCellValueFactory(new PropertyValueFactory<>("packagename"));

        debloaterTableView.getColumns().setAll(checkTableColumn, appTableColumn, packageTableColumn);
        debloaterTableView.refresh();
    }
    
    public ObservableList<App> getApps(){
        comm = new Command();
        String installed = comm.exec("adb shell pm list packages");
        ObservableList<App> apps = FXCollections.observableArrayList();
        apps.add(new App("Analytics", "com.miui.analytics"));
        apps.add(new App("App Vault", "com.miui.personalassistant"));
        apps.add(new App("App Vault", "com.mi.android.globalpersonalassistant"));
        apps.add(new App("Apps (Mi App Store)", "com.xiaomi.mipicks"));
        apps.add(new App("Browser", "com.android.browser"));
        apps.add(new App("Calculator", "com.google.android.calculator"));
        apps.add(new App("Calculator", "com.miui.calculator"));
        apps.add(new App("Calendar", "com.android.calendar"));
        apps.add(new App("Calendar", "com.google.android.calendar"));
        apps.add(new App("Cleaner", "com.miui.cleanmaster"));
        apps.add(new App("Clock", "com.android.deskclock"));
        apps.add(new App("Clock", "com.google.android.deskclock"));
        apps.add(new App("Compass", "com.miui.compass"));
        apps.add(new App("Direct Service / Quick Apps", "com.miui.hybrid"));
        apps.add(new App("Downloads", "com.android.providers.downloads.ui"));
        apps.add(new App("Facebook", "com.facebook.katana"));
        apps.add(new App("Facebook App Installer", "com.facebook.system"));
        apps.add(new App("Facebook App Manager", "com.facebook.appmanager"));
        apps.add(new App("Facebook Services", "com.facebook.services"));
        apps.add(new App("Feedback", "com.miui.bugreport"));
        apps.add(new App("File Manager", "com.mi.android.globalFileexplorer"));
        apps.add(new App("File Manager", "com.android.fileexplorer"));
        apps.add(new App("Files", "com.android.documentsui"));
        apps.add(new App("FM Radio", "com.miui.fm"));
        apps.add(new App("Gmail", "com.google.android.gm"));
        apps.add(new App("Google App", "com.google.android.googlequicksearchbox"));
        apps.add(new App("Google Assistant", "com.google.android.apps.googleassistant"));
        apps.add(new App("Google Chrome", "com.android.chrome"));
        apps.add(new App("Google Drive", "com.google.android.apps.docs"));
        apps.add(new App("Google Duo", "com.google.android.apps.tachyon"));
        apps.add(new App("Google Hangouts", "com.google.android.talk"));
        apps.add(new App("Google Indic Keyboard", "com.google.android.apps.inputmethod.hindi"));
        apps.add(new App("Google Keep", "com.google.android.keep"));
        apps.add(new App("Google Korean Input", "com.google.android.inputmethod.korean"));
        apps.add(new App("Google Maps", "com.google.android.apps.maps"));
        apps.add(new App("Google Photos", "com.google.android.apps.photos"));
        apps.add(new App("Google Pinyin Input", "com.google.android.inputmethod.pinyin"));
        apps.add(new App("Google Play Books", "com.google.android.apps.books"));
        apps.add(new App("Google Play Games", "com.google.android.play.games"));
        apps.add(new App("Google Play Movies", "com.google.android.videos"));
        apps.add(new App("Google Play Music", "com.google.android.music"));
        apps.add(new App("Google Zhuyin Input", "com.google.android.apps.inputmethod.zhuyin"));
        apps.add(new App("KLO Bugreport", "com.miui.klo.bugreport"));
        apps.add(new App("Mab", "com.xiaomi.ab"));
        apps.add(new App("Mail", "com.android.email"));
        apps.add(new App("Mi AI", "com.miui.voiceassist"));
        apps.add(new App("Mi Cloud", "com.miui.cloudservice"));
        apps.add(new App("Mi Cloud Backup", "com.miui.cloudbackup"));
        apps.add(new App("Mi Credit", "com.xiaomi.payment"));
        apps.add(new App("Mi Drop", "com.xiaomi.midrop"));
        apps.add(new App("Mi Roaming", "com.miui.virtualsim"));
        apps.add(new App("Mi Video", "com.miui.video"));
        apps.add(new App("Mi Video", "com.miui.videoplayer"));
        apps.add(new App("Mi Wallet", "com.mipay.wallet"));
        apps.add(new App("MiuiDaemon", "com.miui.daemon"));
        apps.add(new App("Mobile Device Information Provider", "com.amazon.appmanager"));
        apps.add(new App("Msa", "com.miui.msa.global"));
        apps.add(new App("Msa", "com.miui.systemAdSolution"));
        apps.add(new App("Music", "com.miui.player"));
        apps.add(new App("Notes", "com.miui.notes"));
        apps.add(new App("PAI", "android.autoinstalls.config.Xiaomi.tissot"));
        apps.add(new App("Recorder", "com.android.soundrecorder"));
        apps.add(new App("Scanner", "com.xiaomi.scanner"));
        apps.add(new App("Screen Recorder", "com.miui.screenrecorder"));
        apps.add(new App("Search", "com.android.quicksearchbox"));
        apps.add(new App("Weather", "com.miui.weather2"));
        apps.add(new App("Xiaomi Account", "com.xiaomi.vipaccount"));
        apps.add(new App("Xiaomi SIM Activate Service", "com.xiaomi.simactivate.service"));
        apps.add(new App("Yellow Pages", "com.miui.yellowpage"));
        apps.add(new App("YouTube", "com.google.android.youtube"));
        for (Iterator<App> iterator = apps.iterator(); iterator.hasNext();) {
            if (!installed.contains(iterator.next().packagenameProperty().get() + System.lineSeparator()))
                iterator.remove();
        }
        return apps;
    }
    
    @FXML
    private void checkMenuItemPressed(ActionEvent event) {
        checkDevice();
    }

    @FXML
    private void reboottwrpButtonPressed(ActionEvent event) {
        if (checkADB()){
            comm = new Command();
            if (comm.exec("adb devices").contains("recovery")){
                outputTextArea.setText("Device already in recovery mode!");
            } else {
                comm = new Command(outputTextArea);
                comm.exec("adb reboot recovery");
            }
        }
    }

    @FXML
    private void disableButtonPressed(ActionEvent event) {
        if (checkADB()) {
            comm = new Command();
            if (!comm.exec("adb devices").contains("recovery")){
                outputTextArea.setText("ERROR: No device found in recovery mode!");
                return;
            }
            comm.exec("adb shell setprop persist.camera.HAL3.enabled 0");
            if(!checkcamera2())
                outputTextArea.setText("Disabled!");
            else outputTextArea.setText("ERROR: Couldn't disable!");
        }
    }

    @FXML
    private void enableButtonPressed(ActionEvent event) {
        if (checkADB()) {
            comm = new Command();
            if (!comm.exec("adb devices").contains("recovery")){
                outputTextArea.setText("ERROR: No device found in recovery mode!");
                return;
            }
            comm.exec("adb shell setprop persist.camera.HAL3.enabled 1");
            if(checkcamera2())
                outputTextArea.setText("Enabled!");
            else outputTextArea.setText("ERROR: Couldn't enable!");
        }
    }
    
    @FXML
    private void disableEISButtonPressed(ActionEvent event) {
        if (checkADB()) {
            comm = new Command();
            if (!comm.exec("adb devices").contains("recovery")){
                outputTextArea.setText("ERROR: No device found in recovery mode!");
                return;
            }
            comm.exec("adb shell setprop persist.camera.eis.enable 0");
            if(!checkEIS())
                outputTextArea.setText("Disabled!");
            else outputTextArea.setText("ERROR: Couldn't disable!");
        }
    }

    @FXML
    private void enableEISButtonPressed(ActionEvent event) {
        if (checkADB()) {
            comm = new Command();
            if (!comm.exec("adb devices").contains("recovery")){
                outputTextArea.setText("ERROR: No device found in recovery mode!");
                return;
            }
            comm.exec("adb shell setprop persist.camera.eis.enable 1");
            if(checkEIS())
                outputTextArea.setText("Enabled!");
            else outputTextArea.setText("ERROR: Couldn't enable!");
        }
    }

    @FXML
    private void readpropertiesButtonPressed(ActionEvent event) {
        if (checkADB()){
            comm = new Command(outputTextArea);
            comm.exec("adb shell getprop");
        }
    }

    @FXML
    private void savepropertiesButtonPressed(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Text File (.txt)", "*.txt");
        fc.getExtensionFilters().add(fileExtensions);
        fc.setTitle("Save properties");
        File f = fc.showSaveDialog(((Node)event.getSource()).getScene().getWindow());
        if (f != null){
            FileWriter fw = null;
            comm = new Command();
            try {
                fw = new FileWriter(f);
                fw.write(comm.exec("adb shell getprop"));
                fw.flush();
                fw.close();
            } catch (IOException ex) {
                System.out.println("ERROR: Couldn't write file.");
            }
        }
    }
    
    @FXML
    private void antirbButtonPressed(ActionEvent event) {
        if (checkFastboot()) {
            comm = new Command(outputTextArea);
            comm.exec("fastboot flash antirbpass dummy.img");
        }
    }

    @FXML
    private void browseimageButtonPressed(ActionEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("Image File", "*.*");
        fc.getExtensionFilters().add(fileExtensions);
        fc.setTitle("Select an image");
        File f = fc.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (f != null) {
            image = f.getAbsolutePath();
            imageLabel.setText(image.substring(image.lastIndexOf(File.separator)+1));
        }
    }

    @FXML
    private void flashButtonPressed(ActionEvent event) {
        if (image.length() > 1 && partitionComboBox.getValue() != null && partitionComboBox.getValue().trim().length() > 0 && checkFastboot()) {
            comm = new Command(outputTextArea);
            if (autobootCheckBox.isSelected() && partitionComboBox.getValue().trim() == "recovery")
            	comm.exec("fastboot flash " + partitionComboBox.getValue().trim() + " " + image, "fastboot boot " + image);
            else comm.exec("fastboot flash " + partitionComboBox.getValue().trim() + " " + image);
        }
    }

    @FXML
    private void bootButtonPressed(ActionEvent event) {
        if (image.length() > 1 && checkFastboot()) {
            comm = new Command(outputTextArea);
            comm.exec("fastboot boot " + image);
        }
    }

    @FXML
    private void cacheButtonPressed(ActionEvent event) {
        if (checkFastboot()) {
            comm = new Command(outputTextArea);
            comm.exec("fastboot erase cache");
        }
    }

    @FXML
    private void cachedataButtonPressed(ActionEvent event) {
        if (checkFastboot()) {
            comm = new Command(outputTextArea);
            comm.exec("fastboot erase cache", "fastboot erase userdata");
        }
    }

    @FXML
    private void lockButtonPressed(ActionEvent event) {
        if (checkFastboot()) {
            comm = new Command(outputTextArea);
            comm.exec("fastboot oem lock");
        }
    }

    @FXML
    private void unlockButtonPressed(ActionEvent event) {
        if (checkFastboot()) {
            comm = new Command(outputTextArea);
            comm.exec("fastboot oem unlock");
        }
    }
    
    @FXML
    private void systemMenuItemPressed(ActionEvent event) {
        if (checkADB()){
            comm = new Command(outputTextArea);
            comm.exec("adb reboot");
        } else if (checkFastboot()) {
            comm = new Command(outputTextArea);
            comm.exec("fastboot reboot");
        }
    }

    @FXML
    private void recoveryMenuItemPressed(ActionEvent event) {
        if (checkADB()) {
            comm = new Command(outputTextArea);
            comm.exec("adb reboot recovery");
        }
    }

    @FXML
    private void fastbootMenuItemPressed(ActionEvent event) {
        if (checkADB()) {
            comm = new Command(outputTextArea);
            comm.exec("adb reboot bootloader");
        } else if (checkFastboot()) {
            comm = new Command(outputTextArea);
            comm.exec("fastboot reboot bootloader");
        }
    }

    @FXML
    private void edlMenuItemPressed(ActionEvent event) {
        if (checkADB()) {
            comm = new Command(outputTextArea);
            comm.exec("adb reboot edl");
        } else if (checkFastboot()) {
            comm = new Command(outputTextArea);
            comm.exec("fastboot oem edl");
        }
    }
    
    @FXML
    private void uninstallButtonPressed(ActionEvent event) {
        if (checkADB()){
            progressBar.setProgress(-1);
            Thread t = new Thread(() -> {
                Uninstaller comm = new Uninstaller(outputTextArea);
                for (App app : debloaterTableView.getItems()) {
                    if (app.selectedProperty().get()) {
                        comm.uninstall(app);
                    }
                }
                Platform.runLater(() -> {
                    createTable();
                    progressBar.setProgress(0);
                });
            });
            t.start();
        }
    }

    @FXML
    private void addButtonPressed(ActionEvent event) {
        if (customappTextField.getText() != null && customappTextField.getText().trim().length() > 1)
            debloaterTableView.getItems().add(new App(customappTextField.getText().trim(), customappTextField.getText().trim()));
        customappTextField.setText(null);
        debloaterTableView.refresh();
    }
    
    @FXML
    private void aboutMenuItemPressed(ActionEvent event) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.initStyle(StageStyle.UTILITY);
    	alert.setTitle("About");
    	alert.setGraphic(new ImageView(new Image(this.getClass().getClassLoader().getResource("smallicon.png").toString())));
    	alert.setHeaderText("Xiaomi ADB/Fastboot Tools" + System.lineSeparator() + "Version 3.0.0" + System.lineSeparator() + "Created by Saki_EU");
    	VBox vb = new VBox();
    	vb.setAlignment(Pos.CENTER);
    	
    	Hyperlink reddit = new Hyperlink("r/Xiaomi on Reddit");
    	reddit.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent e) {
    	    	try {
					Desktop.getDesktop().browse(new URI("https://www.reddit.com/r/Xiaomi"));
				} catch (IOException | URISyntaxException e1) {
					System.out.println("ERROR: Couldn't open website!");
				}
    	    }
    	});
    	reddit.setFont(new Font(14));
    	Hyperlink discord = new Hyperlink("r/Xiaomi on Discord");
    	discord.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent e) {
    	    	try {
					Desktop.getDesktop().browse(new URI("https://discord.gg/xiaomi"));
				} catch (IOException | URISyntaxException e1) {
					System.out.println("ERROR: Couldn't open website!");
				}
    	    }
    	});
    	discord.setFont(new Font(14));
    	Hyperlink github = new Hyperlink("This project on GitHub");
    	github.setOnAction(new EventHandler<ActionEvent>() {
    	    @Override
    	    public void handle(ActionEvent e) {
    	    	try {
					Desktop.getDesktop().browse(new URI("https://github.com/Saki-EU/XiaomiADBFastbootTools"));
				} catch (IOException | URISyntaxException e1) {
					System.out.println("ERROR: Couldn't open website!");
				}
    	    }
    	});
    	github.setFont(new Font(14));
    	    
    	vb.getChildren().addAll(reddit, discord, github);
    	alert.getDialogPane().setContent(vb);
    	alert.showAndWait();
    }
    
}
