package player;

public class Wave {
	private final boolean bigEndian;
	private final boolean signed;
	int bits;
	int channels;
	double sampleRate;
	
	public Wave() {
		this.bigEndian = false; //формат wav
		this.signed = true;
		this.bits = 16;
		this.channels  = 2;
		this.sampleRate= 44100.0;
	}
	
	public boolean isBigEndian() { //старший байт
		return this.bigEndian;
	}
	
	public boolean isSigned() { //знаковый
		return this.signed;
	}
	
	public int getBits() { //разрядность
		return this.bits;
	}
	
	public int getChannels() { //каналы
		return this.channels;
	}
	
	public double getSampleRate() { //частота дискретизации
		return this.sampleRate;
	}
			
	
}
