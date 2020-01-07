package gui;

import player.Player;
import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.*;
import javafx.stage.Stage;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.*;


public class FXMLController implements Initializable {
     
	public static boolean closed = false;
    @FXML 
    private Slider Slider0, Slider1, Slider2, Slider3, Slider4, Slider5, Slider6, Slider7, distSlider, reverbSlider,soundSlider;
    @FXML 
    private Label Label0, Label1, Label2, Label3, Label4, Label5, Label6, Label7, name, reverbonoff, distonoff;
    
    @FXML
    private LineChart chart1;
    @FXML
    private LineChart chart2;
    @FXML
    private NumberAxis xAxis1, yAxis1, xAxis2, yAxis2;
    private XYChart.Data[] oldSignal1;
    private XYChart.Data[] oldSignal2;
    private XYChart.Data[] newSignal1;
    private XYChart.Data[] newSignal2;
    private boolean graphButton = false;
    
    public Player audioPlayer;
    private Thread playThread, plotThread;
    
    @FXML
    private ProgressBar progressBar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.listenSliders();
        this.initialGraph();
        this.distSlider();
        this.reverbSlider();
        this.volumeFromSlider();
        this.progressBar.setProgress(0.0);
    }
    
    
    @FXML
    private void distButton() {
    	System.out.println("DISTORTION");
    	if (this.audioPlayer.getDistortion()) {
    		this.audioPlayer.setDistortion(false);
    		distonoff.setText("Off");
    	}
    	else {
    		this.audioPlayer.setDistortion(true);
    		distonoff.setText("On");
    	}
    }
    
    
    @FXML
    private void reverbButton() {
    	System.out.println("REVERBERATION");
    	if (this.audioPlayer.getReverb()) {
    		this.audioPlayer.setReverb(false);
    		reverbonoff.setText("Off");
    	}
    	else {
    		this.audioPlayer.setReverb(true);
    		reverbonoff.setText("On");
    	}
    }
    
    @FXML
    private void open() throws FileNotFoundException, IOException, LineUnavailableException, UnsupportedAudioFileException, InterruptedException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Формат файла", "*.wav"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        
        name.setText(selectedFile.getName());
        
        Slider0.setValue(1.0);
        Slider1.setValue(1.0);
        Slider2.setValue(1.0);
        Slider3.setValue(1.0);
        Slider4.setValue(1.0);
        Slider5.setValue(1.0);
        Slider6.setValue(1.0);
        Slider7.setValue(1.0);
        reverbonoff.setText("Off");
        distonoff.setText("Off");
        distSlider.setValue(1000);
        reverbSlider.setValue(170);
        soundSlider.setValue(0.75);
        
        if(selectedFile == null) return;
        if (this.audioPlayer == null) {
        	this.audioPlayer = new Player(selectedFile);
        	this.audioPlayer.setProgressBar(progressBar);
        	
        	playThread = new Thread(()->{
        		this.audioPlayer.play();
        		});
        	playThread.start();
        	System.out.println("PLAY");
        }
        else {
        	if(this.playThread != null)
        		this.playThread.interrupt();
        	this.audioPlayer.getEqualizer1().close();
        	this.audioPlayer.getEqualizer2().close();
        	this.audioPlayer.close();
        	this.audioPlayer = new Player(selectedFile);
        	this.progressBar.setProgress(0.0);
        	this.audioPlayer.setProgressBar(progressBar);
        	playThread = new Thread(()->{this.audioPlayer.play();});
        	playThread.start();
        	System.out.println("PLAY");
        }
    }
    
    @FXML
    private void play() {
        if (this.audioPlayer != null)
        	System.out.println("PLAY");
            this.audioPlayer.setPause(false);
    }
    
    @FXML
    private void pause() {
        if (this.audioPlayer != null)
        	 System.out.println("PAUSE");  
            this.audioPlayer.setPause(true);
    }
    
    @FXML
    private void stop() {
        if (this.audioPlayer != null)
        	 System.out.println("STOP");  
    	if(this.playThread != null)
    		this.playThread.interrupt();
    	this.audioPlayer.getEqualizer1().close();
    	this.audioPlayer.getEqualizer2().close();
    	this.audioPlayer.close();
    	this.progressBar.setProgress(0.0);
    }
    
    @FXML
    private void reset() {
        if (this.audioPlayer == null) return;
        Slider0.setValue(1.0);
        Slider1.setValue(1.0);
        Slider2.setValue(1.0);
        Slider3.setValue(1.0);
        Slider4.setValue(1.0);
        Slider5.setValue(1.0);
        Slider6.setValue(1.0);
        Slider7.setValue(1.0);
        distSlider.setValue(1000);
        reverbSlider.setValue(170);
        soundSlider.setValue(0.75);
        this.audioPlayer.setDistortion(false);
        this.audioPlayer.setReverb(false);
        reverbonoff.setText("Off");
        distonoff.setText("Off");
        System.out.println("RESET");
    }
    
    @FXML
    private void clickClose() {
    	if(this.audioPlayer != null) {
            if(this.playThread != null)
        	this.playThread.interrupt();
            this.audioPlayer.getEqualizer1().close();
            this.audioPlayer.getEqualizer2().close();
            this.audioPlayer.close();
    	}
    	
    	System.exit(0);
    }
    
    private void listenSliders(){
        Slider0.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer1().getFilter((short)0).setGain((float)newValue.doubleValue());
            audioPlayer.getEqualizer2().getFilter((short)0).setGain((float)newValue.doubleValue());
        });
        
        Slider1.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer1().getFilter((short)1).setGain((float)newValue.doubleValue());
            audioPlayer.getEqualizer2().getFilter((short)1).setGain((float)newValue.doubleValue());
        });
        
        Slider2.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer1().getFilter((short)2).setGain((float)newValue.doubleValue());
            audioPlayer.getEqualizer2().getFilter((short)2).setGain((float)newValue.doubleValue());
        });
        
        Slider3.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer1().getFilter((short)3).setGain((float)newValue.doubleValue());
            audioPlayer.getEqualizer2().getFilter((short)3).setGain((float)newValue.doubleValue());
        });
        
        Slider4.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer1().getFilter((short)4).setGain((float)newValue.doubleValue());
            audioPlayer.getEqualizer2().getFilter((short)4).setGain((float)newValue.doubleValue());
        });
        
        Slider5.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer1().getFilter((short)5).setGain((float)newValue.doubleValue());
            audioPlayer.getEqualizer2().getFilter((short)5).setGain((float)newValue.doubleValue());
        });
        
        Slider6.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer1().getFilter((short)6).setGain((float)newValue.doubleValue());
            audioPlayer.getEqualizer2().getFilter((short)6).setGain((float)newValue.doubleValue());
        });
        
        Slider7.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioPlayer.getEqualizer1().getFilter((short)7).setGain((float)newValue.doubleValue());
            audioPlayer.getEqualizer2().getFilter((short)7).setGain((float)newValue.doubleValue());
        });
        

    }
    
    private void distSlider() {
        distSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            audioPlayer.setAmplitudeDistortion(newValue.doubleValue());
        });
    }

    private void reverbSlider() {
        reverbSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            audioPlayer.setTimeReverb(newValue.doubleValue());
        });
    }
    
    private void volumeFromSlider() {
        soundSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            audioPlayer.setVolume(newValue.doubleValue());
        });
    }
    

    
    private void initialGraph() {
        XYChart.Series oldGraph1 = new XYChart.Series<>();
        XYChart.Series oldGraph2 = new XYChart.Series<>();
        XYChart.Series newGraph1 = new XYChart.Series<>();
        XYChart.Series newGraph2 = new XYChart.Series<>();
        int size = 128;
        
        oldSignal1 = new XYChart.Data[size];
        oldSignal2 = new XYChart.Data[size];
        newSignal1 = new XYChart.Data[size];
        newSignal2 = new XYChart.Data[size];
        
        for (int i = 0; i < oldSignal1.length; i++){
        	
            oldSignal1[i] = new XYChart.Data<>(i, 0);
            oldGraph1.getData().add(oldSignal1[i]);
            oldSignal2[i] = new XYChart.Data<>(i, 0);
            oldGraph2.getData().add(oldSignal2[i]);
            
            newSignal1[i] = new XYChart.Data<>(i, 0);
            newGraph1.getData().add(newSignal1[i]);
            newSignal2[i] = new XYChart.Data<>(i, 0);
            newGraph2.getData().add(newSignal2[i]);
        }
        
        chart1.getData().add(oldGraph1);
        chart1.getData().add(oldGraph2);
        chart2.getData().add(newGraph1);
        chart2.getData().add(newGraph2);
        
        chart1.setCreateSymbols(false);
        chart2.setCreateSymbols(false);
        
        chart1.setAnimated(false);
        chart2.setAnimated(false);
        
        this.chart1.getYAxis();
        this.yAxis1.setLowerBound(-0.3);
        this.yAxis1.setUpperBound(0.3);
        this.yAxis1.setAnimated(false);
        
        
        this.chart2.getYAxis();
        this.yAxis2.setLowerBound(-0.3);
        this.yAxis2.setUpperBound(0.3);
        this.yAxis2.setAnimated(false);  
    }

    
    @FXML
    private void clickPlot() {
        if (this.graphButton == true) {
            this.graphButton = false;
        }
        else this.graphButton = true;
        
        this.plotThread = new Thread(()->{
            while(this.graphButton) {
                if(this.graphButton == false) {
                    for(;;){
                        try{
                            if(this.graphButton == true) break;
                            this.plotThread.sleep(50);
                        }
                        catch(Exception e){
                        }
                    }
                }
                
                if(audioPlayer.isCalculated) {
                	for(int j = 0; j < (this.audioPlayer.getFourierInput1().length)/2; j++){
                		this.oldSignal1[j].setYValue(Math.log10(this.audioPlayer.getFourierInput1()[j] / Short.MAX_VALUE) * 20);
                		this.oldSignal2[j].setYValue(Math.log10(this.audioPlayer.getFourierInput2()[j] / Short.MAX_VALUE) * 20);
                        this.newSignal1[j].setYValue(Math.log10(this.audioPlayer.getFourierOutput1()[j] / Short.MAX_VALUE) * 20);
                        this.newSignal2[j].setYValue(Math.log10(this.audioPlayer.getFourierOutput2()[j] / Short.MAX_VALUE) * 20);
                    }
                }
                
                try {
                    this.plotThread.sleep(60);
                } 
                catch (Exception e) {
                }
            }
        });
        plotThread.start();
    }
}