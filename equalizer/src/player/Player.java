package player;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import equalizer.Equalizer;
import effects.Reverb;
import effects.Distortion;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.scene.control.ProgressBar;

public class Player implements LineListener{
	private double volume;
	private final SourceDataLine sourceDataLine;
	private final AudioInputStream ais;
	private float timeMax;
	private float time = 0;
	private ProgressBar progressBar;
	private final byte[] bytes;
	private final byte[] outbytes;    

    private final int BUFF_SIZE = 2048;
    private final int READ_SIZE = 1024; 
    
	private short[] sampleBuff1;
	private short[] sampleBuff2;
	private short[] outsampleBuff1;
	private short[] outsampleBuff2;
	
    private final FFT fourierInput1;
    public FFT fourierOutput1;
    
    private final FFT fourierInput2;
    public FFT fourierOutput2;
    
    public boolean isCalculated = false;
    
	private Queue isampleBuffer1;
	private Queue isampleBuffer2;
	private Queue osampleBuffer1;
	private Queue osampleBuffer2;
	
	private final Equalizer equalizer1;
	private Reverb reverb1;
	private Distortion distortion1;
	
	private final Equalizer equalizer2;
	private Reverb reverb2;
	private Distortion distortion2;
	
	private boolean dist = false;
	private boolean rever = false;
	private double reverbTime = 170;
	
	private boolean pause;
	private final AudioFormat format;
        
	public Player(File musicFile) throws UnsupportedAudioFileException, IOException, InterruptedException, LineUnavailableException {
		ReadMusic readFile = new ReadMusic(musicFile);
		this.sourceDataLine =  readFile.getSourceDataLine();
		this.ais = readFile.getAudioInputStream();
		
		this.timeMax = ais.getFrameLength() / ais.getFormat().getFrameRate();
		
        this.bytes = new byte[this.READ_SIZE];
        this.outbytes = new byte[this.READ_SIZE];
        
		this.sampleBuff1 = new short[READ_SIZE / 4];
		this.sampleBuff2 = new short[READ_SIZE / 4];
		this.outsampleBuff1 = new short[READ_SIZE / 4];
		this.outsampleBuff2 = new short[READ_SIZE / 4];
		
		this.isampleBuffer1 = new Queue(BUFF_SIZE);
		this.isampleBuffer2 = new Queue(BUFF_SIZE);
		this.osampleBuffer1 = new Queue(BUFF_SIZE);
		this.osampleBuffer2 = new Queue(BUFF_SIZE);
		
		this.isampleBuffer1.initQueue();
		this.isampleBuffer2.initQueue();
		this.osampleBuffer1.initQueue();
		this.osampleBuffer2.initQueue();
		
		this.equalizer1 = new Equalizer(READ_SIZE / 4);
		this.reverb1 = new Reverb(READ_SIZE/4, reverbTime);
		this.distortion1 = new Distortion(READ_SIZE/4);
		
		this.equalizer2 = new Equalizer(READ_SIZE / 4);
		this.reverb2 = new Reverb(READ_SIZE/4, reverbTime);
		this.distortion2 = new Distortion(READ_SIZE/4);
		
		Wave aff = new Wave();
		this.format = new AudioFormat((float)aff.getSampleRate(), aff.getBits(), aff.getChannels(), aff.isSigned(), aff.isBigEndian());
		this.volume = 1.0;
        this.fourierInput1 = new FFT();
        this.fourierOutput1 = new FFT();
        this.fourierInput2 = new FFT();
        this.fourierOutput2 = new FFT();
	}
        
	public void play() {
			try{
				this.sourceDataLine.open(this.format); 
				this.sourceDataLine.start();
				this.pause = false;
				while ((this.ais.read(this.bytes, 0, this.READ_SIZE)) != -1) { //читаем байты с файла
					
					this.ByteArrayToSamplesArray();
					
					this.isCalculated = false;
					this.fourierInput1.FFTAnalysis(this.sampleBuff1, 256);
					this.fourierInput2.FFTAnalysis(this.sampleBuff2, 256);
                    
            		for(int i = 0; i < READ_SIZE/4; i++) {
            			this.isampleBuffer1.deleteFromBuffer();
            			this.isampleBuffer1.addToRing(sampleBuff1[i]);
            			this.isampleBuffer2.deleteFromBuffer();
            			this.isampleBuffer2.addToRing(sampleBuff2[i]);
            		}
                    
					if(this.pause) {this.stop();}
					
					this.equalizer1.setInputSignal(this.isampleBuffer1);
					this.equalizer2.setInputSignal(this.isampleBuffer2);
					this.equalizer1.equalization(this.osampleBuffer1);
					this.equalizer2.equalization(this.osampleBuffer2);
                    
					if (getDistortion()) {
						this.distortion1.processEffect(this.osampleBuffer1);
						this.distortion2.processEffect(this.osampleBuffer2);
					}
					
					if (getReverb()) {
						this.reverb1.processEffect(this.osampleBuffer1);
						this.reverb2.processEffect(this.osampleBuffer2);
					}
					
					this.outsampleBuff1  = this.osampleBuffer1.getElements(READ_SIZE/4);
					this.outsampleBuff2  = this.osampleBuffer2.getElements(READ_SIZE/4);
					
					this.fourierOutput1.FFTAnalysis(this.outsampleBuff1, 256);
					this.fourierOutput2.FFTAnalysis(this.outsampleBuff2, 256);
                    this.isCalculated = true;
                    
					this.SampleArrayByteArray();
					
					this.time += (READ_SIZE/(this.ais.getFormat().getFrameSize())) / this.ais.getFormat().getFrameRate();
					this.progressBar.setProgress((double)this.getTime());
					this.sourceDataLine.write(this.outbytes, 0, READ_SIZE);
				}
				this.isCalculated = false;
				System.out.println("END");
				this.sourceDataLine.drain();
				this.sourceDataLine.close();

			}catch (LineUnavailableException | IOException | InterruptedException | ExecutionException e) {
			}
	}
	
	private void stop() {
		if(pause) {
			for(;;) {
				try {
					if(!pause) break;

					Thread.sleep(50);
				} 
                catch (InterruptedException e) {
				}
			}
		}
	}
	
	public void setPause(boolean pause) {
		this.pause = pause;
	}
	
	public boolean getPause() {
		return this.pause;
	}
	
	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public double getVolume() {
		return this.volume;
	}
	
	public void setDistortion(boolean bool) {
		this.dist = bool;
	}
	
	public void setAmplitudeDistortion(double value) {
		this.distortion1.setAmplitude((short)value);
		this.distortion2.setAmplitude((short)value);
	}
	
	public void setReverb(boolean bool) {
		this.rever = bool;
	}
	
	public void setTimeReverb(double value) {
		this.reverbTime = value;
		this.reverb1= new Reverb(READ_SIZE/4, reverbTime);
		this.reverb2= new Reverb(READ_SIZE/4, reverbTime);
	}
	
	public boolean getDistortion() {
		return this.dist;
	}
	
	public boolean getReverb() {
		return this.rever;
	}
	
	@Override
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
	}

	
	private void ByteArrayToSamplesArray() {
		for(int i = 0, j = 0, k = 2; k < READ_SIZE; i += 4, k += 4, j++) {
			this.sampleBuff1[j] = (short) (0.5 *  (ByteBuffer.wrap(this.bytes, i, 2).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort()) * this.getVolume());
			this.sampleBuff2[j] = (short) (0.5 *  (ByteBuffer.wrap(this.bytes, k, 2).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort()) * this.getVolume());
		}
	}
	
	private void SampleArrayByteArray() {
		for(int i = 0, j = 0; i < READ_SIZE/4 && j < (this.outbytes.length); i++, j += 4 ) {
			this.outbytes[j] = (byte)(this.outsampleBuff1[i]);
			this.outbytes[j + 1] = (byte)(this.outsampleBuff1[i] >>> 8);
			this.outbytes[j + 2] = (byte)(this.outsampleBuff2[i]);
			this.outbytes[j + 3] = (byte)(this.outsampleBuff2[i] >>> 8);
		}
	}
	
	public Equalizer getEqualizer1() {
		return this.equalizer1;
	}
	
	public Equalizer getEqualizer2() {
		return this.equalizer2;
	}
	
	public void close() {
		if(this.ais != null)
			try {
				this.ais.close();
			} 
            catch (IOException e) {
			}
		if(this.sourceDataLine != null)
			this.sourceDataLine.close();
	}
	
	public float getTime() {
		return this.time/this.timeMax;
	}
	
	public void setProgressBar(ProgressBar progress) {
		this.progressBar = progress;
	}
	
    public double[] getFourierInput1(){
        return this.fourierInput1.getFFTData();
    }
    
    public double[] getFourierInput2(){
        return this.fourierInput2.getFFTData();
    }
    
    public double[] getFourierOutput1(){
        return this.fourierOutput1.getFFTData();
    }
    
    public double[] getFourierOutput2(){
        return this.fourierOutput2.getFFTData();
    }
}
